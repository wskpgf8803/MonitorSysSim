package tinghua.hpc;

import java.util.ArrayList;
import java.util.List;

/**
 * AutoBucket used in leaderboard
 * Time: 2012-04-18
 *
 * @author Yu. He
 */



public class AutoBucket {
    public final static int bucketCapacity =1000;
    public final static int NUMBER = 1000000;

    //
    public static int getRank(List<List<UserScore>> bucketList, int score) {
        List<UserScore> buck=null;
        int postLen=0;
        int buckIdx=-1;
        synchronized (bucketList)
        {
            if(score==0)//内存中不存储零分
            {
                return size(bucketList)+1;
            }
            buckIdx = getBucketIdx(bucketList, score);
            buck=bucketList.get(buckIdx);
            if(buck==null)return -1;
            for (int i = 0; i < buckIdx; i++) {
                postLen += bucketList.get(i).size();
            }
        }
        synchronized (buck)
        {
            int subIdx = binarySearchByScore(buck, score);
            return subIdx + 1  + postLen;
        }
    }

    //
    public static void updateScore(List<List<UserScore>> bucketList,int userId, int oldScore, int newScore) {
        if(newScore==0)return;//零分不存储
        synchronized (bucketList)
        {
            int idx=searchByUserId(bucketList,userId,oldScore);
            if(idx>0)
            {
                remove(bucketList,idx);
                add(bucketList, new UserScore(userId, newScore));
            }else
            {
                add(bucketList, new UserScore(userId, newScore));
            }
        }
    }

    //
    private static int searchByUserId(List<List<UserScore>> bucketList,int userId,int oldScore)//oldScore不正确，将无法查找到正确的位置
    {
        int bucketIdx=getBucketIdx(bucketList,oldScore);
        
        int preIdx=0;
        for(int i=0;i<bucketIdx;i++)
        {
            preIdx+=bucketList.get(i).size();
        }
        
        int len=bucketList.size();
        int subIdx=-1;
        for(int n=bucketIdx;n<len;n++)
        {
            List<UserScore> buck=bucketList.get(n);
            if(buck==null)return -1;
            if(n>bucketIdx)
            {
                if(buck.get(0).getScore()!=oldScore)
                {
                    break;
                }else{
                	for(int i=1;i<buck.size();i++)
                    {
                        UserScore us=buck.get(i);
                        if(us.getUserId()==userId)
                        {
                            if(us.getScore()!=oldScore)
                            {
                                return -1;
                            }else{
                            	return preIdx+i;
                            }
                        }
                    }
                }
            }else{
                int bsubIdx=binarySearchByScore(buck,oldScore);
                for(int i=bsubIdx;i<buck.size();i++)
                {
                    UserScore us=buck.get(i);
                    if(us.getUserId()==userId)
                    {
                        if(us.getScore()!=oldScore)
                        {
                            return -1;
                        }else{
                        	return preIdx+i;
                        }
                    }
                }
            }
        }
        if(subIdx==-1)return -1;
        return preIdx+subIdx;
    }
    private static int searchByUserId(List<List<UserScore>> bucketList,int userId)//能获取userId正确位置，但速度很慢
    {
        return 0;
    }
    //
    private static int size(List<List<UserScore>> bucketList) {
        int num = bucketList.size();
        int size = 0;
        for (int i = 0; i < num; i++) {
            size += bucketList.get(i).size();
        }
        return size;
    }
    
    //
    private static void add(List<List<UserScore>> bucketList, UserScore us) {
        if (bucketList.isEmpty()) {
            bucketList.add(new ArrayList<UserScore>());
            bucketList.get(0).add(us);
            return;
        }
        int buckIdx = getBucketIdx(bucketList, us.getScore());
        if (bucketList.get(buckIdx).isEmpty()) {
            bucketList.get(buckIdx).add(us);
            return;
        }
        int pos = binarySearchByScore(bucketList.get(buckIdx), us.getScore());
        bucketList.get(buckIdx).add(pos, us);
        if (bucketList.get(buckIdx).size() > bucketCapacity) {
            spiltBucket(bucketList, buckIdx);
        }
    }
    //
    private static UserScore get(List<List<UserScore>> bucketList,int idx)
    {
        if(idx<0)return null;
        int num = bucketList.size();
        int size = 0;
        int buckIdx=0;
        int subIdx=0;
        boolean find=false;
        for (int i = 0; i < num; i++) {
            int tmp=size;
            size += bucketList.get(i).size();
            if(size>idx)
            {
                buckIdx=i;
                subIdx=idx-tmp;
                find=true;
                break;
            }
        }
        if(!find)
        {
            return null;
        }
        List<UserScore> buck=bucketList.get(buckIdx);
        return buck.get(subIdx);
    }
    //
    private static void remove(List<List<UserScore>> bucketList,int idx)
    {
        if(idx<0)return;
        int num = bucketList.size();
        int size = 0;
        int buckIdx=0;
        int subIdx=0;
        boolean find=false;
        for (int i = 0; i < num; i++) {
            int tmp=size;
            size += bucketList.get(i).size();
            if(size>idx)
            {
                buckIdx=i;
                subIdx=idx-tmp;
                find=true;
                break;
            }
        }
        if(!find)
        {
            return;
        }
        List<UserScore> buck=bucketList.get(buckIdx);
        buck.remove(subIdx);
        if(buck.size()==0)
        {
            bucketList.remove(buckIdx);
        }
    }
	/**
	 * 查找score应该放入bucket中的位置,不管socre存不存在
	 * @param bucket
	 * @param score
	 * @return
	 */
    private static int binarySearchByScore(List<UserScore> bucket, int score) {
        int len = bucket.size();
        int left = 0, right = len - 1, mid = (left + right) / 2;

        while (left < right) {
            if (bucket.get(mid).getScore() == score) {  //查找到数据，如果有重复数据，返回最小的idx
                mid--;
                while (mid > 0 && bucket.get(mid).getScore() == score) {
                    mid--;
                }
                return mid + 1;
            }else if (bucket.get(mid).getScore() < score) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
            mid = (left + right) / 2;
        }
        //如果没有查找到数据，返回该数据在把此数据加到list后的idx
        if (bucket.get(left).getScore() > score)
            return  left + 1;
        else
            return left;
    }

    
    //
    private static void spiltBucket(List<List<UserScore>> bucketList, int buckIdx) {
        ArrayList<UserScore> bucketTemp = new ArrayList<UserScore>(bucketList.get(buckIdx).subList(bucketCapacity / 2, bucketList.get(buckIdx).size()));
        bucketList.set(buckIdx, new ArrayList<UserScore>(bucketList.get(buckIdx).subList(0, bucketCapacity / 2)));
        bucketList.add(buckIdx + 1, bucketTemp);

    }
    
    /**
     * 
     * @param bucketList
     * @param score
     * @return 为score所在bucket的位置,改为二分查找
     */
    private static int getBucketIdx(List<List<UserScore>> bucketList, int score) {
        int i ;
        int len = bucketList.size();
        if(len <= 1)return 0;
//        for (i = len - 1; i > 0; i--) {
//            if (score < bucketList.get(i).get(0).getScore())
//                break;
//        }
//        return i;
        
        int left = 0, right = len - 1, mid = (left + right) / 2;
        while (left < right) {
        	if(bucketList.get(mid).get(0).getScore() == score){
        		mid--;
                while (mid > 0 && bucketList.get(mid).get(0).getScore() == score) {
                    mid--;
                }
        		return mid + 1;
        	}
            if(bucketList.get(mid).get(0).getScore() <= score) {
                right = mid - 1;
            } else {
                left = mid + 1;
            }
            mid = (left + right) / 2;
        }
        
        if (bucketList.get(left).get(0).getScore() > score)
            return  left;
        else
            return (left - 1 > 0)?left - 1:0;
        
    }


    public static boolean isSeq(List<List<UserScore>> bucketList) {
        int num = 0;
        int pre = Integer.MAX_VALUE;
        for (int i = 0; i < bucketList.size(); i++) {
            int len = bucketList.get(i).size();
            num += len;
            for (int j = 0; j < len; j++) {
                if (j == 0) {
                    if (bucketList.get(i).get(j).getScore() > pre) {
                        System.out.println("not sequence");
                        return false;
                    } else
                        continue;
                }
                if (j == len - 1) {
                    pre = bucketList.get(i).get(j).getScore();
                    continue;
                }
                if (bucketList.get(i).get(j + 1).getScore() > bucketList.get(i)
                        .get(j).getScore()) {
                    System.out.println("not sequence");
                    return false;
                }
            }
        }
        if (num != size(bucketList)) {
            System.out.println("not same size");
            return false;
        }
        return true;

    }

    public static void print(List<List<UserScore>> bucketList) {
        for (int i = 0; i < bucketList.size(); i++) {
            int len = bucketList.get(i).size();
            for (int j = 0; j < len; j++) {
                System.out.print("userId:"+bucketList.get(i).get(j).getUserId()+" score:"+bucketList.get(i).get(j).getScore());
                System.out.print(" ");
            }
            System.out.println("buck-"+i);
        }
        System.out.println();
    }

    public static  void main(String args[])
    {
    	long totalmem = Runtime.getRuntime().totalMemory();
    	long startTime = System.nanoTime();
    	List<List<UserScore>>  bucketList = new ArrayList<List<UserScore>>();
        AutoBucket autoBucket = new AutoBucket();
        for(int i = 1; i <= NUMBER; i ++){
        	UserScore us = new UserScore(i, i);
        	AutoBucket.add(bucketList, us);
        }

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("time = " + estimatedTime + " ns");
        System.out.println("memory = " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory() - totalmem )+ " B");

        System.out.println("over!");
 
    }
}
