package ru.byprogminer.compmath.lab1.utils

import java.math.BigInteger

tailrec fun gcd(a: BigInteger, b: BigInteger): BigInteger =
		if (b == BigInteger.ZERO) {
			a.abs()
		} else {
			gcd(b, a % b)
		}
