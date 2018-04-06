import redis.clients.jedis.Jedis;

public class Main {

    public Main(){
    }

    public void test(int userIndex){
        HandleSteal handleSteal = new HandleSteal(userIndex);
        handleSteal.initCounterFromRedis();
        Worker worker= new Worker(handleSteal);
        worker.run();
    }

    public static void main(String[] args){
        int userIndex = 1;
        if(args.length >= 1){
            userIndex  = Integer.parseInt(args[0]);
        }
        Main main = new Main();
        main.test(userIndex);
    }
}
