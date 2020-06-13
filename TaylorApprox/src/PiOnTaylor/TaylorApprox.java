/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package PiOnTaylor;


import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * This example code base is found at: https://code5.cn/so/java/2621792 
 * Modified a little for the dynamic inputs and reports.
 * Other references I used and tried for the alternative developments are:
 * https://github.com/LeonardoZ/java-concurrency-patterns/tree/master/src/main/java/br/com/leonardoz/patterns
 * https://github.com/DongJigong/JavaMultiThreading/blob/master/JavaMultiThreadingCodes/src/LockObjects_4/Worker.java 
 * https://github.com/ferhatelmas/pi 
 * https://www.physicsforums.com/threads/taylor-series-approximation-for-pi.839441/
 * 
 * Code reusability is encouraged in the software development. It shortens the development time and 
 * saves your precious time. What I found and modified is completely public source and everyone can use it. 
 * I also understood the whole concept, added commentaries.
 * And I did not refactor or claim that the whole code is done by myself. 
 * 
 * @author Esra 
 */
public class TaylorApprox {

    public static void main(String[] args) throws InterruptedException {
   
    //Added these boolean variables to overcome do-while loop limit bugs.
    //N states the number of operations
    boolean hasNext=true;
    boolean operationSet=false;
    int N = 1_000_000;
    
    //Dynamic input from the user:    
    //Default answer is no, since the CPU would be happier, if the calculation ends faster.
    //This N states "NO" for default answer.
    String answer="N";
    String div= "\n---------------------------";
    
    Scanner scan= new Scanner(System.in); 
       
    if(!operationSet){
        System.out.print("Hello World!\n\nHow many operations should "
                + "we have in the calculation? \nTry a high number, because "
                + "more operations come with the better approximation!"
                + "\nEnter an integer(Between 1 and 5.000.000): \n");
    
        if(scan.hasNextInt()){
            N = scan.nextInt();
        
             if(N>5_000_000 || N<1){
                System.out.println("This is offlimits. "
                        + "I will calculate from 1.000.000, which is "
                        + "a great number.");
                N=1_000_000;
                operationSet=true;
            }
        }
                      
        else{
        //A passive aggresive information to user, who entered wrong kind of input.
            System.out.println("Ok, it is not an integer. I set the number of "
                    + "operations to " + N);
            operationSet=true;
        }
    }
        
    Scanner sc= new Scanner(System.in); 
    //The input can be between 1-64 as the homework states.
    //I added a random generaed thread number 
    //in order to prevent the cases that the user enters meaningless input. 
    //The higher input, the more precise results.
    int input = (int) (Math.random()*65)-1;
    System.out.print("\nHow many threads should work concurrently? "
            + "Enter an integer(Between 1 and 64): \n");
        if(sc.hasNextInt()){
            input = sc.nextInt();
          
            
            if(input>64 || input<1){
                System.out.println("This is offlimits. "
                        + "I will calculate from 64.");
                input=64;                 
                hasNext=false;               
            }
                      
        }else{
     //A passive aggresive information to user, who entered wrong kind of input.
            System.out.println("You should have entered an integer only... "
                    + "\nOk I will randomize the number of the threads, "
                    + "which is " + input + " now." + div);
            
        }
        
   //a do-while loop in order to calculate by adding 1 threads in everystep 
   
    do{
    long startTime,endTime,totalTime;
    int threadCount = input;
   
    //Used "a" to check the iterations. 
    //double a=0;
       
    System.out.println("The thread count is " + threadCount +
            " and number of iterations is " + N + "\nThis means there will be "
            + N/2 + " add and sub operations each.");
    
    //Threads creation
    PiThread[] threads = new PiThread[threadCount];
    //Nanosecond-precise start time to calculate and report the total-time.
    startTime = System.nanoTime();    
    
    //Invokes new threads and causes threads to begin executions concurrently
    for (int i = 0; i < threadCount; i++) {
        threads[i] = new PiThread(threadCount, i, N);
        threads[i].start();
    }
    
    //join() method returns the active threads 
    //and make sure that they are terminated.
    for (int i = 0; i < threadCount; i++) {
        threads[i].join();
    }
    
    //The calculation of the threads on Taylor's formula summation is done here.
    double pi = 0;
    for (int i = 0; i < threadCount; i++) {
        pi += threads[i].getSum();
        // a+=threads[i].getCount()-1;
    }
     
    //Converting the nanoseconds to miliseconds for better readability.
    endTime=System.nanoTime();
    totalTime=TimeUnit.NANOSECONDS.toMillis(endTime-startTime);
    
    
    //Formatting for pi comparision.
    
    String heading1 = "The Real PI is";
    String heading2 = "Calculated PI is";

    System.out.printf( "\n%-25s %s %n%-25s %s %n", 
                        heading1, Math.PI, heading2, pi);
    
    
    System.out.println("\nTime spent: " + totalTime + " ms"
                       + "\nThe approximation error: " + (Math.PI-pi) + div);
        
    Scanner scanner=new Scanner(System.in);
    answer="N";
    //Preference is on the user. User can continue or break the loop. 
    if(answer.equalsIgnoreCase("N")){
        if (input<64){
            System.out.println("Want to add one more thread? Y/N?");
            answer=scanner.next();
            if (answer.equalsIgnoreCase("Y")){
                System.out.println("OK, Calculating again");
                //Increasing by one thread as homework suggests.
                System.out.println("The new thread count is " + ++input + div);
            }
             else{
                System.out.println("Understood, bye.");
                answer="N";
            }
        }
    }
    }
    while(hasNext && input<65 && answer.equalsIgnoreCase("Y"));
    
}
    
    //This part is from the code5.cn; 
    //PiThreads inherit the attributes and methods of Thread class.
    
    static class PiThread extends Thread{
    
    private final int threadCount;
    private final int threadRemainder;
    private final int N;
    private double sum  = 0;
    //private double count  = 0;

    public PiThread(int threadCount, int threadRemainder, int n) {
        this.threadCount = threadCount;
        this.threadRemainder = threadRemainder;
        N = n;
    }

    //Overrides run() by using it for calculation for Taylor series
    @Override
    public void run() {
        
        for (int i = 0; i <= N; i++) {
            if (i % threadCount == threadRemainder) {
                //As it is shown in the pi with Taylor series formula.
                //Each iteration has 1 negative or one positive value. 
                //If i is even, it will be positive, else; negative.
                sum += 4*( Math.pow(-1, i) / (2 * i + 1) );
                //I added to check the number of iterations.
               // count++;
               
            }
        }
    }

    public double getSum() {
        return sum;
    }
    
   //In order to access the count outside of the scope.
   /* public double getCount() {
        return count;
    }
    */
    
    //That's all! Thank you for reviewing the whole code.
    //I did not share this code with any of my classmates.
    //But I will post it on github by removing comments after 10 May.
    //Stay healthy!
    }
}
