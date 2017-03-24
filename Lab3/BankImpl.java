public class BankImpl {
	private int numberOfCustomers;	// the number of customers
	private int numberOfResources;	// the number of resources

	private int[] available; 	// the available amount of each resource
	private int[][] maximum; 	// the maximum demand of each customer
	private int[][] allocation;	// the amount currently allocated
	private int[][] need;		// the remaining needs of each customer
	
	public BankImpl (int[] resources, int numberOfCustomers) {
		// TODO: set the number of resources
		this.numberOfResources = resources.length;
		// TODO: set the number of customers
		this.numberOfCustomers = numberOfCustomers;
		// TODO: set the value of bank resources to available
		this.available = resources;
		// TODO: set the array size for maximum, allocation, and need
		this.maximum = new int[numberOfCustomers][numberOfResources];
		this.allocation = new int[numberOfCustomers][numberOfResources];
		this.need = new int[numberOfCustomers][numberOfResources];
	}
	
	public int getNumberOfCustomers() {
		// TODO: return numberOfCustomers
		return this.numberOfCustomers;
	}

	public void addCustomer(int customerNumber, int[] maximumDemand) {
		// TODO: add customer, update maximum and need
		for(int i = 0; i < this.numberOfResources; i++){
			this.maximum[customerNumber][i] = maximumDemand[i];
			this.need[customerNumber][i] = maximumDemand[i];
		}
	}

	public void getState() {
		// TODO: print the current state with a tidy format
		System.out.println("Current state: ");
		// TODO: print available
		System.out.println("Available: ");
		for (int a : available) {
			System.out.print(a + " ");
		}
		System.out.println(" ");
		// TODO: print maximum
		System.out.println("Maximum: ");
		for (int[] m: maximum) {
			for (int m1: m) {
				System.out.print(m1 + " ");
			}
			System.out.println(" ");
		}
		// TODO: print allocation
		System.out.println("Allocation: ");
		for (int[] a : allocation) {
			for (int a1 : a) {
				System.out.print(a1 + " ");
			}
			System.out.println(" ");
		}
		// TODO: print need
		System.out.println("Need: ");
		for (int[] n : need) {
			for (int n1 : n) {
				System.out.print(n1 + " ");
			}
			System.out.println(" ");
		}
	}

	public synchronized boolean requestResources(int customerNumber, int[] request) {
		// TODO: print the request
		System.out.println("Customer " + customerNumber + " requests for: ");
		for (int r : request) {
			System.out.print(r + " ");
		}
		System.out.println(" ");

		// TODO: check if request larger than need
		for (int i = 0; i < this.numberOfResources; i++) {
			if (request[i] > this.need[customerNumber][i]) {
				System.out.println("Request for resource " + i + " is invalid.");
				return false;
			}
		}
		// TODO: check if request larger than available
		for (int i = 0; i < this.numberOfResources; i++) {
			if (request[i] > this.available[i]) {
				System.out.println("Not enough resource " + i + " to grant request.");
				return false;
			}
		}
		// TODO: check if the state is safe or not
		if (!this.checkSafe(customerNumber, request)) {
			System.out.println("Request denied (system unsafe)");
			return false;
		}
		// TODO: if it is safe, allocate the resources to customer customerNumber 
		for (int i = 0; i < this.numberOfResources; i++) {
			this.need[customerNumber][i] -= request[i];
			this.available[i] -= request[i];
			this.allocation[customerNumber][i] += request[i];
		}
		System.out.println("Request granted.");
		return true;
	}

	public synchronized void releaseResources(int customerNumber, int[] release) {
		// TODO: print the release
		System.out.println("Releasing resources: ");
		// release the resources from customer customerNumber 
		for (int i = 0; i < this.numberOfResources; i++) {
			this.available[i] += release[i];
			this.allocation[customerNumber][i] -= release[i];
			this.need[customerNumber][i] += release[i];
			System.out.print(i + " ");
		}
	}

	private synchronized boolean checkSafe(int customerNumber, int[] request) {
		// TODO: check if the state is safe
		int[] temp_avail = new int[this.numberOfResources]; // temporary available
		int[][] temp_need = new int[this.numberOfCustomers][this.numberOfResources]; // temporary need
		int[][] temp_allocation = new int[this.numberOfCustomers][this.numberOfResources]; // temporary allocation
		int[] work = new int[this.numberOfResources];
		boolean[] finish = new boolean[this.numberOfCustomers]; // Java initialises this as false by default

		// Step 1: Initialising the values for Work, Available, Need and Allocation
		for (int j = 0; j < this.numberOfResources; j++) {
			temp_avail[j] = this.available[j] - request[j];
			work[j] = temp_avail[j];
			for (int i = 0; i < this.numberOfCustomers; i++) {
				if (i == customerNumber) {
					temp_need[customerNumber][j] = this.need[customerNumber][j] - request[j];
					temp_allocation[customerNumber][j] = this.allocation[customerNumber][j]  + request[j];
				} else {
					temp_need[i][j] = this.need[i][j];
					temp_allocation[i][j] = this.allocation[i][j];
				}
			}
		}

		// Step 2: Finding an i such that Finish[i] = false and Need[i] <= Work
		boolean i_exists = true;
		while (i_exists) {
			for (int i = 0; i < this.numberOfCustomers; i++) {

				// Checking if Need[i] <= Work
				boolean enoughResources = true;
				for (int j = 0; j < this.numberOfResources; j++) {
					if ((temp_need[i][j] > work[j])) {
						enoughResources = false; // will only be true if every resource satisfies
						break;
					}
				}
				i_exists = false;

				// Checking if Finish[i] = false and Need[i] <= Work
				if (!finish[i] && enoughResources) {
					i_exists = true;
					// Stop 3: Work = Work + Allocation[i] and Finish[i] = true
					for (int j = 0; j < this.numberOfResources; j++) {
						work[j] += temp_allocation[i][j];
					}
					finish[i] = true;
				}
			}
		}

		// Step 4: Determine if safe
		boolean safe = true;
		for (int i = 0; i < this.numberOfCustomers; i++) {
			if (!finish[i]) {
				safe = false;
				break;
			}
		}
		return safe;
	}
}