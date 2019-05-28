package aseos;

import java.util.concurrent.*;
public class Aseo {

	Semaphore nMenWomenProtector = new Semaphore(1, true);
	Semaphore deadlockPreventer = new Semaphore(1, true);
	int nMen = 0;
	int nWomen = 0;
	Semaphore blockWomen = new Semaphore(1, true);
	Semaphore blockMen = new Semaphore(1, true);
	/**
	 * El hombre id quiere entrar en el aseo. 
	 * Espera si no es posible, es decir, si hay alguna mujer en ese
	 * momento en el aseo
	 */
	public void llegaHombre(int id) throws InterruptedException{
		deadlockPreventer.acquire();
		blockMen.acquire();
		nMenWomenProtector.acquire();
		nMen++;
		if(nMen == 1) // the first man blocks women
			blockWomen.acquire();
		System.out.println("Man "+id+" enters.");
		nMenWomenProtector.release();
		blockMen.release();
		deadlockPreventer.release();
	}
	/**
	 * La mujer id quiere entrar en el aseo. 
	 * Espera si no es posible, es decir, si hay algun hombre en ese
	 * momento en el aseo
	 */
	public void llegaMujer(int id) throws InterruptedException{
		deadlockPreventer.acquire();
		blockWomen.acquire();
		nMenWomenProtector.acquire();
		nWomen++;
		if(nWomen == 1) // the first woman blocks men
			blockMen.acquire();
		System.out.println("Woman "+id+" enters.");
		nMenWomenProtector.release();
		blockWomen.release();
		deadlockPreventer.release();
	}
	/**
	 * El hombre id, que estaba en el aseo, sale
	 */
	public void saleHombre(int id)throws InterruptedException{
		nMenWomenProtector.acquire();
		nMen--;
		System.out.println("Man "+id+" exits.");
		if (nMen == 0)
			blockWomen.release();
		nMenWomenProtector.release();
	}
	
	public void saleMujer(int id)throws InterruptedException{
		nMenWomenProtector.acquire();
		nWomen--;
		System.out.println("Woman "+id+" exits.");
		if (nWomen == 0)
			blockMen.release();
		nMenWomenProtector.release();
	}
}
