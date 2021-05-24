package com.java.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

/**
 * TimerTask 定时任务
 * 
 * 		scheduleAtFixedRate(TimerTask task, Date firstTime, long period)
 * 			注意：如果firstTime<当前时间，则启动程序时会连续执行 （当前时间-firstTime）%period 次，然后再每隔period执行一次
 */
public class Timer_ {

    private SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws ParseException {
        new Timer_();
    }

    /**
     * 配置定时器
     * @throws ParseException
     */
    public Timer_() throws ParseException {
        Timer timer = new Timer();
        OrderTask orderTask = new OrderTask(timer);
        //指定时间执行一次
        timer.schedule(orderTask, df.parse("2018-08-13 12:12:00"));
    }
}

/**
 * 定时任务
 */
class OrderTask extends TimerTask {

    private Timer timer;
    public OrderTask(Timer timer){
        this.timer = timer;
    }

    @Override
    public void run() {
        System.out.println("正在测试Timer定时任务！");
        timer.cancel(); //终止任务
    }
}