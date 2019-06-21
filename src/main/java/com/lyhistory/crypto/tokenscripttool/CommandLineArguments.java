package com.lyhistory.crypto.tokenscripttool;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

import org.web3j.crypto.Hash;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;

public class CommandLineArguments {
	private static final String OPT = "--";
	private static final String TRUST_ARG = "trust";
	private static final String REVOKE_ARG = "revoke";
	private static final String PUBLICKEY_ARG = "pubkey";
	private static final String HELP_ARG = "help";

	@Parameter(names = OPT + TRUST_ARG)
	private String trust;
	@Parameter(names = OPT + REVOKE_ARG)
	private String revoke;
	@Parameter(names = OPT + PUBLICKEY_ARG)
	private String pubkey;

	@Parameter(names = HELP_ARG, help = true)
	private boolean help;

	private BigInteger digestBI;

	private boolean isValidateArgs;

	public boolean doHelp() {
		return help;
	}

	public boolean isValidateArgs() {
		return isValidateArgs;
	}

	public String getType() {
		if (trust != null) {
			return "TRUST";
		} else {
			return "REVOKE";
		}
	}

	public String getPubKey() {
		return this.pubkey;
	}

	public void setPubKey(String pubkey) {
		this.pubkey = pubkey;
	}

	public BigInteger getDigestBI() {
		return this.digestBI;
	}

	public void validateArgs() {
		try {
			if (trust != null && revoke != null || trust == null && revoke == null) {
				isValidateArgs = false;
			} else {
				String digest = trust != null ? trust : revoke;
				digest = Util.convertHexToBase64String(digest);
				byte[] target = String.format("%s%s", this.getType(), digest).getBytes(StandardCharsets.UTF_8);
				//System.out.println("target:" + target.toString());
				byte[] h_digest = Hash.sha3(target);
				//System.out.println("h_digest:" + h_digest.toString());
				//System.out.println("encodeHexString:" + Util.convertByteArrayToHex(h_digest));

				this.digestBI = new BigInteger(Util.convertByteArrayToHex(h_digest), 16);

				isValidateArgs = true;
			}
		} catch (Exception ex) {
			isValidateArgs = false;
		}
	}

	public void parseCommandLineArguments(final String[] args) {
		try {
			final JCommander jc = new JCommander(this);
			jc.parse(args);

			validateArgs();
		} catch (final ParameterException e) {
			System.out.println(e.getMessage());
			this.isValidateArgs=false;
		}
	}
}
