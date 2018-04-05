public class Main {

    public Main(){
    }

    public void test(int userIndex,int mode,int modeTime,int delayCompare,int beforeCompare){
        HandleSteal handleSteal = new HandleSteal(userIndex,mode,modeTime,delayCompare,beforeCompare);
        //KeepAlive keepAlive = new KeepAlive();
        //StealRobber stealRobber = new StealRobber();
        Worker worker= new Worker(handleSteal,6);
        worker.run();
    }

    public static void main(String[] args){
        int userIndex = 1;
        int mode = 1;
        int modeTime = 175;
        int delayCompare = 270;
        int beforeCompare = 1255;
        if(args.length >= 5){
            userIndex  = Integer.parseInt(args[0]);
            mode = Integer.parseInt(args[1]);
            modeTime = Integer.parseInt(args[2]);
            delayCompare =  Integer.parseInt(args[3]);
            beforeCompare =  Integer.parseInt(args[4]);

        }
        System.out.println("userIndex=" + userIndex + ",mode=" + mode + ",modeTime=" + modeTime + ",delayCompare=" + delayCompare + ",beforeCompare=" + beforeCompare);
        Main main = new Main();
        main.test(userIndex,mode,modeTime,delayCompare,beforeCompare);
    }
}
