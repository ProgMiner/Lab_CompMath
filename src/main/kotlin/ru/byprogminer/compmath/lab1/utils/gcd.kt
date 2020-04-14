package ru.byprogminer.compmath.lab1.utils

import java.math.BigInteger

tailrec fun gcd(a: BigInteger, b: BigInteger): BigInteger =
		if (b != BigInteger.ZERO) {
			gcd(b, a % b)
		} else {
			a.abs()
		}
