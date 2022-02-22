import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class MyBakeryLock implements Lock {

	volatile boolean[] flag;
	volatile int[] label;
	int n;

	public MyBakeryLock(int n) {
		flag = new boolean[n];
		label = new int[n];
		this.n = n;
		
		for (int i = 0; i < n; i++) {
			flag[i] = false;
			label[i] = 0;
		}
	}

	@Override
	public void lock() {
		int i = ThreadID.get();
		flag[i] = true;
		int maximum = 0;
		for(int num : label) {
			if(num > maximum) maximum = num;
		}
		maximum = maximum + 1 ;
		for (int j = 0; j < n; j++) {
			while((j != i) && flag[j] && ((label[i] < label[j]) || ((label[j] == label[i]) && j < i)));
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {

	}


	@Override
	public void unlock() {
		int i = ThreadID.get();
		flag[i] = false;
	}

	@Override
	public Condition newCondition() {
		return null;
	}


	@Override
	public boolean tryLock() {
		int i = ThreadID.get();
		if(!flag[i]){
			lock();
			return true;
		}
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
		return false;
	}
}
