class HelloWorld implements Runnable
{ 

	public static void main(String argv[]) throws Exception 
    { 
		HelloWorld hw = new HelloWorld();
		
		Thread t0 = new Thread(hw);
		t0.setName("T0");
		t0.start();
		
		Thread t1 = new Thread(hw);
		t1.setName("T1");
		t1.start();
		
		Thread t2 = new Thread(hw);
		t2.setName("T2");
		t2.start();
    }
	
	public HelloWorld ()
	{
		System.out.println("Creating object");
	}
	
	public void run()
	{
		this.SayHello();
	}
	
	public synchronized void SayHello()
	{
		int sum = 0;
		for(int i=0; i<100; i++)
		{
			sum += i;
		}
		System.out.println("Thread: " + Thread.currentThread().getName() + " Hello World!");
	}
	
} 
 