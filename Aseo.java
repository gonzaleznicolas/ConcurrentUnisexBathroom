package aseos;

import java.util.concurrent.*;
public class Aseo {

	Semaphore intMutex = new Semaphore(1, true);
	Semaphore deadlockPreventer = new Semaphore(1, true);
	int nMenInBathroom = 0;
	int nWomenInBathroom = 0;
	int nPeopleWhoHaveExited = 0;
	boolean hadToKickGenderOut = false;
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
		intMutex.acquire();
		nMenInBathroom++;
		if(nMenInBathroom == 1) // the first man blocks women
		{
			blockWomen.acquire();
			nPeopleWhoHaveExited = 0;
			hadToKickGenderOut = false;
		}
		System.out.println("Man "+id+" enters.");
		intMutex.release();
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
		intMutex.acquire();
		nWomenInBathroom++;
		if(nWomenInBathroom == 1) // the first woman blocks men
		{
			blockMen.acquire();
			nPeopleWhoHaveExited = 0;
			hadToKickGenderOut = false;
		}
		System.out.println("Woman "+id+" enters.");
		intMutex.release();
		blockWomen.release();
		deadlockPreventer.release();
	}
	/**
	 * El hombre id, que estaba en el aseo, sale
	 */
	public void saleHombre(int id)throws InterruptedException{
		intMutex.acquire();
		nMenInBathroom--;
		nPeopleWhoHaveExited++;
		if (nPeopleWhoHaveExited >= 5 && hadToKickGenderOut == false)
		{
			blockMen.acquire();
			hadToKickGenderOut = true;
			System.out.println("Not letting new men in.");
		}
		System.out.println("Man "+id+" exits. Currently "+nMenInBathroom+" men in bathroom. "+nPeopleWhoHaveExited+" have exited.");
		if (nMenInBathroom == 0){
			blockWomen.release();
			if (hadToKickGenderOut == true)
				blockMen.release();
		}
		intMutex.release();
	}
	
	public void saleMujer(int id)throws InterruptedException{
		intMutex.acquire();
		nWomenInBathroom--;
		nPeopleWhoHaveExited++;
		if (nPeopleWhoHaveExited >= 5 && hadToKickGenderOut == false)
		{
			blockWomen.acquire();
			hadToKickGenderOut = true;
			System.out.println("Not letting new women in.");
		}
		System.out.println("Woman "+id+" exits. Currently "+nWomenInBathroom+" women in bathroom. "+nPeopleWhoHaveExited+" have exited.");
		if (nWomenInBathroom == 0){
			blockMen.release();
			if (hadToKickGenderOut == true)
				blockWomen.release();
		}
		intMutex.release();
	}
}
