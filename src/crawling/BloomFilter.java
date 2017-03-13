package crawling;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


class BloomFilter {
	private byte[] set;
	private int keySize, setSize, size;
	private MessageDigest md;

	public BloomFilter(int capacity, int k) {
		setSize = capacity;
		set = new byte[setSize];
		keySize = k;
		size = 0;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Error : MD5 Hash not found");
		}
	}

	public void makeEmpty() {
		set = new byte[setSize];
		size = 0;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException("Error : MD5 Hash not found");
		}
	}

	public boolean isEmpty() {
		return size == 0;
	}

	public int getSize() {
		return size;
	}

	private int getHash(int i) {
		md.reset();
		byte[] bytes = ByteBuffer.allocate(4).putInt(i).array();
		md.update(bytes, 0, bytes.length);
		return Math.abs(new BigInteger(1, md.digest()).intValue()) % (set.length - 1);
	}

	public void add(Object obj) {
		int[] tmpset = getSetArray(obj);
		for (int i : tmpset)
			set[i] = 1;
		size++;
	}

	public boolean contains(Object obj) {
		int[] tmpset = getSetArray(obj);
		for (int i : tmpset)
			if (set[i] != 1)
				return false;
		return true;
	}

	private int[] getSetArray(Object obj) {
		int[] tmpset = new int[keySize];
		tmpset[0] = getHash(obj.hashCode());
		for (int i = 1; i < keySize; i++)
			tmpset[i] = (getHash(tmpset[i - 1]));
		return tmpset;
	}
}
