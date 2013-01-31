package net.ion.nsearcher.common;

public class HashFunction {

	public static long hashWebContent(CharSequence cseq){
		
		long hash = 5381; // seed value

		for (int i = 0; i < cseq.length(); i++) {
			char c = cseq.charAt(i) ;
			if (c >= '0' && c <= '9') continue ;
			hash = ((hash << 5) + hash) + c;
		}

		return hash;
	}
	
	
	public static long hashGeneral(CharSequence str) {
		long hash = str.length();

		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);
		}

		return hash;
	}
	
	
	
	
	// A simple hash function from Robert Sedgwicks Algorithms in C book. 
	// I've added some simple optimizations to the algorithm in order to speed up its hashing process. 
	private long RSHash(String str) {
		int b = 378551;
		int a = 63689;
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = hash * a + str.charAt(i);
			a = a * b;
		}

		return hash;
	}

	// A bitwise hash function written by Justin Sobel 
	private long JSHash(String str) {
		long hash = 1315423911;

		for (int i = 0; i < str.length(); i++) {
			hash ^= ((hash << 5) + str.charAt(i) + (hash >> 2));
		}

		return hash;
	}

	// This hash algorithm is based on work by Peter J. Weinberger of AT&T Bell Labs. 
	// The book Compilers (Principles, Techniques and Tools) by Aho, Sethi and Ulman, 
	// recommends the use of hash functions that employ the hashing methodology found in this particular algorithm. 
	private long PJWHash(String str) {
		long BitsInUnsignedInt = (long) (4 * 8);
		long ThreeQuarters = (long) ((BitsInUnsignedInt * 3) / 4);
		long OneEighth = (long) (BitsInUnsignedInt / 8);
		long HighBits = (long) (0xFFFFFFFF) << (BitsInUnsignedInt - OneEighth);
		long hash = 0;
		long test = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash << OneEighth) + str.charAt(i);

			if ((test = hash & HighBits) != 0) {
				hash = ((hash ^ (test >> ThreeQuarters)) & (~HighBits));
			}
		}

		return hash;
	}

	// Similar to the PJW Hash function, but tweaked for 32-bit processors. 
	// Its the hash function widely used on most UNIX systems.
	private long ELFHash(String str) {
		long hash = 0;
		long x = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash << 4) + str.charAt(i);

			if ((x = hash & 0xF0000000L) != 0) {
				hash ^= (x >> 24);
			}
			hash &= ~x;
		}

		return hash;
	}

	// This hash function comes from Brian Kernighan and Dennis Ritchie's book "The C Programming Language". 
	// It is a simple hash function using a strange set of possible seeds which all constitute a pattern of 31....31...31 etc, 
	// it seems to be very similar to the DJB hash function. 
	private long BKDRHash(String str) {
		long seed = 131; // 31 131 1313 13131 131313 etc..
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = (hash * seed) + str.charAt(i);
		}

		return hash;
	}

	// This is the algorithm of choice which is used in the open source SDBM project. 
	// The hash function seems to have a good over-all distribution for many different data sets. It seems to work well in situations where there is a high variance in the MSBs of the elements in a data set. 
	private long SDBMHash(String str) {
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = str.charAt(i) + (hash << 6) + (hash << 16) - hash;
		}

		return hash;
	}

	// An algorithm produced by Professor Daniel J. Bernstein and shown first to the world on the usenet newsgroup comp.lang.c. 
	// It is one of the most efficient hash functions ever published. 
	private long DJBHash(String str) {
		long hash = 5381;

		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) + hash) + str.charAt(i);
		}

		return hash;
	}

	// An algorithm proposed by Donald E. Knuth in The Art Of Computer Programming Volume 3, under the topic of sorting and search chapter 6.4. 
	private long DEKHash(String str) {
		long hash = str.length();

		for (int i = 0; i < str.length(); i++) {
			hash = ((hash << 5) ^ (hash >> 27)) ^ str.charAt(i);
		}

		return hash;
	}

	private long BPHash(String str) {
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash = hash << 7 ^ str.charAt(i);
		}

		return hash;
	}

	private long FNVHash(String str) {
		long fnv_prime = 0x811C9DC5;
		long hash = 0;

		for (int i = 0; i < str.length(); i++) {
			hash *= fnv_prime;
			hash ^= str.charAt(i);
		}

		return hash;
	}

	/* An algorithm produced by me Arash Partow. 
	 * I took ideas from all of the above hash functions making a hybrid rotative and additive hash function algorithm. 
	 * There isn't any real mathematical analysis explaining 
	 * why one should use this hash function instead of the others described above other than the fact 
	 * that I tired to resemble the design as close as possible to a simple LFSR. 
	 * 
	 * An empirical result which demonstrated the distributive abilities of the hash algorithm was obtained using a hash-table with 100003 buckets, 
	 * hashing The Project Gutenberg Etext of Webster's Unabridged Dictionary, 
	 * the longest encountered chain length was 7, the average chain length was 2, 
	 * the number of empty buckets was 4579. 
	 * 
	 * Below is a simple algebraic description of the AP hash function: 
	 */
	private long APHash(String str) {
		long hash = 0xAAAAAAAA;

		for (int i = 0; i < str.length(); i++) {
			if ((i & 1) == 0) {
				hash ^= ((hash << 7) ^ str.charAt(i) * (hash >> 3));
			} else {
				hash ^= (~((hash << 11) + str.charAt(i) ^ (hash >> 5)));
			}
		}

		return hash;
	}
}
