/*
 * Bitwise.java - A CodeShane Solution.
 * Copyright (C) 2013 - Shane Robinson (http://codeshane.com/)
 * All rights reserved.
 *
 */
package com.codeshane.util;

/**
 * <p>Bitwise provides static functions for bit-level operations.
 * Similar concept to {@link BitSet}, but designed for the higher
 * performance needs of Android.</p>
 *
 * @see java.util.BitSet
 * @see codeshane.data.Types
 * @see java.security.InvalidParameterException;
 *
 * @author <a href="mailto:shane@codeshane.com">Shane Robinson (@codeshane)</a>
 * @version 1.0, Mar 29, 2013
 */
public class Bitwise {

	/* Flag Constants */

//	public static final long LONG_ALL_SET = ~0L;
//	public static final long LONG_NONE_SET = 0L;
	public static final int INT_ALL_SET = ~0;
	public static final int INT_NONE_SET = 0;

	/**
	 * Bit-order: 0 (First/lowest-order/lowest-value)
	 */
// public static final int BIT_0000 = 0x0001;
// public static final int BIT_0001 = 0x0002;
// public static final int BIT_0002 = 0x0004;
// public static final int BIT_0003 = 0x0008;

	/**
	 * Accepts a base-10 int "bit id", starting from 0 for lowest-order bit, and
	 * returns its base-2 int "bit 'flag'" counterpart. ex: bit(0); // returns
	 * 0000 0000 0000 0001 bit(1); // returns 0000 0000 0000 0010 bit(2); //
	 * returns 0000 0000 0000 0100 bit(3); // returns 0000 0000 0000 1000
	 *
	 * @param bitId
	 *            the common (base-10) id of the bit
	 * @return The value of the chosen bit
	 */
	public static final int bit(int bitId) {
		return 2 ^ bitId;
	}


	/* Bitwise functions */

	/** Bitwise-and : for each bit-position, return true only if a and b bits are true.
	 * <pre>
	 * a 0000 1111 ....
	 * b 0011 0011 ....
	 *
	 * R 0000 0011 ....
	 * </pre>
	 * @param a int bitset
	 * @param b int bitset
	 * @return int bitset
	 * */
	public static int and(int a, int b){ return a & b; }

	/** Bitwise-or : for each bit-position, return true if a and/or b bit is true.
	 * <pre>
	 * a 0000 1111 ....
	 * b 0011 0011 ....
	 *
	 * R 0011 1111 ....
	 * </pre>
	 * @param a int bitset
	 * @param b int bitset
	 * @return int bitset
	 * */
	public static int or(int a, int b){	return a | b; }

	/** Bitwise-exclusive-or : for each bit-position, return true if a or b is true, but false if both are true.
	 * <pre>
	 * a 0000 1111 ....
	 * b 0011 0011 ....
	 *
	 * R 0011 1100 ....
	 * </pre>
	 * @param a int bitset
	 * @param b int bitset
	 * @return int bitset
	 * */
	public static int xor(int a, int b){ return a ^ b; }

	/** Bitwise-complement : returns the bitwise-opposite of a bitset.
	 * Each true bit returns false, and each false bit returns true.
	 * <pre>
	 * bitset    0011 0011 ....
	 *
	 * RETURN    1100 1100 ....</pre>
	 * */
	public static int complement(int bitset) { return ~bitset;}

//	public static int shiftLeftUnsigned(int positions, int bitset) { return bitset <<< positions; }

	/** Shifts bits right; rightmost bits are lost.
	 * */
	public static int shiftRightUnsigned(int positions, int bitset) { return bitset >>> positions; }
	public static int shiftLeftSigned(int positions, int bitset) { return bitset << positions; }

	/** Moves each bit a number of positions to the right.
	 * Right bits are lost; new sign bit equals the sign bit before the shift;
	 * new bits are zero?
	 * */
	public static int shiftRightSigned(int positions, int bitset) { return bitset >> positions; }

	/* Bitset functions */

	/** set() returns the bitset with all true flag-bits set true.<br><br>
	 * Alias for Bitwise-or.
	 * <pre>
	 * flags     0000 1111 ....
	 * bitset    0011 0011 ....
	 *
	 * RETURN    0011 1111 ....
	 * </pre>
	 *
	 * @param flags int flags to set
	 * @param bitset int original bitset
	 * @return int modified bitset
	 * */
	public static int set(int flags, int bitset){ return bitset | flags; }

	/** merge combines two bitsets such that any true bits are reflected in the return value.
	 * Alias for Bitwise-or.
	 * <pre>
	 * flags     0000 1111 ....
	 * bitset    0011 0011 ....
	 *
	 * RETURN    0011 1111 ....
	 * </pre>
	 * */
//	public static int merge(int a, int b){ return a | b; }

	/** toggle() returns the bitset with all true flags-bit positions values reversed.
	 * Alias for Bitwise-xor.
	 *
	 * <pre>
	 * flags     0000 1111 ....
	 * bitset    0011 0011 ....
	 *
	 * RETURN    0011 1100 ....
	 * </pre>
	 * */
	public static int toggle(int flags, int bitset){ return flags ^ bitset; }

	/** unset() returns the bitset with all true flag-bits set false.
	 *
	 * <pre>
	 * flags     0000 1111 ....
	 * bitset    0011 0011 ....
	 *
	 * RETURN    0011 0000 ....
	 * </pre>
	 * @param flags int flags to unset
	 * @param bitset int original bitset
	 * @return int modified bitset
	 * */
	public static int unset(int flags, int bitset){
		// Reverses the flags, then preforms bitwise-and, so only true bits outside the flags can stay true.
		return bitset & ~flags;
	}

	/** isSet() returns true only if all true flag-bits are also true in the bitset.
	 * <pre>
	 * flags     0000 1111 ....
	 * bitset    0011 0011 ....
	 *
	 * RETURN    ---- fftt .... FALSE (some true flag-bits are false in bitset)
	 *
	 * flags     0001 0001 ....
	 * bitset    0011 0011 ....
	 *
	 * RETURN    ---t ---t .... TRUE (all true flag-bits are true in bitset)</pre>
	 * @param flags int flags to check
	 * @param bitset int original bitset
	 * @return boolean = true if all true flag-bits are also true in the bitset.
	 * */
	public static boolean isSet(int flags, int bitset){
		return ((flags & bitset) == flags);
	}

	/** isAnySet() returns true if any true flag-bit is also true in the bitset.
	 * <pre>
	 * flags     0000 1111 ....
	 * bitset    0011 0011 ....
	 *
	 * RETURN    ---- fftt .... TRUE (some true flag-bits are true in bitset)
	 *
	 * flags     0001 0001 ....
	 * bitset    0011 0011 ....
	 *
	 * RETURN    ---t ---t .... TRUE (all true flag-bits are true in bitset)</pre>
	 * @param flags int flags to check
	 * @param bitset int original bitset
	 * @return boolean = true if all true flag-bits are also true in the bitset.
	 * */
	public static boolean isAnySet(int flags, int bitset){
		return ((flags & bitset) != 0);
	}

}
