package com.lyhistory.crypto.tokenscripttool;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.jcajce.provider.asymmetric.util.EC5Util;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.ECPointUtil;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.encoders.Hex;
import org.ethereum.crypto.HashUtil;

import org.web3j.crypto.Hash;

/**
 * Hello world!
 *
 */
public class App {
	static X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
	static ECDomainParameters CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(),
			CURVE_PARAMS.getN(), CURVE_PARAMS.getH());

	static String PUBKEYHEX = "04856747172fcad0f0defbc8ebef218624964791e8431115fd09e74c35cfa2b9111c15511e7c6a2ca10916e97a0befd197de9800b71cb44a96fc5e0ccae0fcd0dd";
	// digest string: RmBmS7FQym2z1x+PGHPbm6bFNXFvoKs5lAskqo2w7Oc=
	// digest base64 encode hex:
	// 4660664bb150ca6db3d71f8f1873db9ba6c535716fa0ab39940b24aa8db0ece7

	public static void main(String[] args) {
		final CommandLineArguments cli = new CommandLineArguments();
		cli.parseCommandLineArguments(args);
		process(cli);
	}

	public static String process(CommandLineArguments cli) {
		addBouncyCastleProvider();
		if (cli.doHelp() || !cli.isValidateArgs()) {
			System.out.println("Usage: --[trust/revoke] ***** -pubkey ****");
			return "help";
		}
		ECPublicKey pubkey = null;
		PUBKEYHEX = cli.getPubKey() == "" ? PUBKEYHEX : cli.getPubKey();
		try {
			//System.out.println("Util.hexToByteArray(pubhex):" + Util.convertHexToByteArray(PUBKEYHEX));
			pubkey = decodeKey(Util.convertHexToByteArray(PUBKEYHEX));

		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ECPoint donatePKPoint = extractPublicKey(pubkey);
		ECPoint digestPKPoint = donatePKPoint.multiply(cli.getDigestBI());

		String type = cli.getType();
		String donateAddr = getAddress(donatePKPoint);
		//System.out.println("donateAddr:" + donateAddr);
		String addr = getAddress(digestPKPoint);
		System.out.println(String.format("%s: %s",
				type.equalsIgnoreCase("TRUST") ? "proofOfTrustAddr" : "proofOfRevokeAddr", addr));
		return addr;
	}

	private static void addBouncyCastleProvider() {
		Security.addProvider(new BouncyCastleProvider());
	}

	private static ECPoint extractPublicKey(ECPublicKey ecPublicKey) {
		java.security.spec.ECPoint publicPointW = ecPublicKey.getW();
		BigInteger xCoord = publicPointW.getAffineX();
		BigInteger yCoord = publicPointW.getAffineY();
		return CURVE.getCurve().createPoint(xCoord, yCoord);
	}

	public static ECPublicKey decodeKey(byte[] encoded)
			throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		ECNamedCurveParameterSpec params = ECNamedCurveTable.getParameterSpec("secp256k1");
		KeyFactory fact = KeyFactory.getInstance("ECDSA", "BC");
		ECCurve curve = params.getCurve();
		java.security.spec.EllipticCurve ellipticCurve = EC5Util.convertCurve(curve, params.getSeed());
		java.security.spec.ECPoint point = ECPointUtil.decodePoint(ellipticCurve, encoded);
		java.security.spec.ECParameterSpec params2 = EC5Util.convertSpec(ellipticCurve, params);
		java.security.spec.ECPublicKeySpec keySpec = new java.security.spec.ECPublicKeySpec(point, params2);
		return (ECPublicKey) fact.generatePublic(keySpec);
	}

	public static String getAddress(ECPoint pub) {
		byte[] pubKeyHash = computeAddress(pub);

		return Hex.toHexString(pubKeyHash);
	}

	public static byte[] computeAddress(byte[] pubBytes) {
		return HashUtil.sha3omit12(Arrays.copyOfRange(pubBytes, 1, pubBytes.length));
	}

	public static byte[] computeAddress(ECPoint pubPoint) {
		return computeAddress(pubPoint.getEncoded(false));
	}

}
