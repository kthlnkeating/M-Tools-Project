package gov.va.med.foundations.utilities;

/**
 * Implements static methods to provide the encoding algorithms used by the RPC Broker and
 * Kernel to encode and decode data strings. Using these algorithms makes it harder to sniff
 * the contents of text sent over the network. This is not, however, encryption-class encoding, nor does it protect
 * against replay attacks of un-decoded strings, and therefore use of this algorithm should not be considered
 * to imply or achieve any particular level of security.
 * <p> For example:
 * <pre>
 * String encodedString = VistaKernelHash.encrypt("some text to encode", true);
 * </pre>
 * 
 * @see VistaKernelHashCountLimitExceededException
 * @author Application Modernization - Foundations Team
 * @version 1.0.0.193
 */
public class VistaKernelHash {

	/**
	 * Holds the number of keys in the cipherPad
	 */
	private static final int maxKey = 20;

	/**
	 * Hold the number of retries before we throw an exception. This is for
	 * when preventEncryptionsContainingCDataSectionBoundaries is TRUE.
	 */
	private static final int countLimit = 2000;
	/**
	 * opening CDATA boundary string
	 */
	private static final String CDATA_BOUNDARY_OPEN = "<![CDATA[";
	/**
	 * closing CDATA boundary string
	 */
	private static final String CDATA_BOUNDARY_CLOSE="]]>";
	/**
	 * Holds the ciphers used by the encoding algorithm
	 */
	private static final String[] cipherPad =
		{
			"wkEo-ZJt!dG)49K{nX1BS$vH<&:Myf*>Ae0jQW=;|#PsO`\'%+rmb[gpqN,l6/hFC@DcUa ]z~R}\"V\\iIxu?872.(TYL5_3",
			"rKv`R;M/9BqAF%&tSs#Vh)dO1DZP> *fX\'u[.4lY=-mg_ci802N7LTG<]!CWo:3?{+,5Q}(@jaExn$~p\\IyHwzU\"|k6Jeb",
			"\\pV(ZJk\"WQmCn!Y,y@1d+~8s?[lNMxgHEt=uw|X:qSLjAI*}6zoF{T3#;ca)/h5%`P4$r]G\'9e2if_>UDKb7<v0&- RBO.",
			"depjt3g4W)qD0V~NJar\\B \"?OYhcu[<Ms%Z`RIL_6:]AX-zG.#}$@vk7/5x&*m;(yb2Fn+l\'PwUof1K{9,|EQi>H=CT8S!",
			"NZW:1}K$byP;jk)7\'`x90B|cq@iSsEnu,(l-hf.&Y_?J#R]+voQXU8mrV[!p4tg~OMez CAaGFD6H53%L/dT2<*>\"{\\wI=",
			"vCiJ<oZ9|phXVNn)m K`t/SI%]A5qOWe\\&?;jT~M!fz1l>[D_0xR32c*4.P\"G{r7}E8wUgyudF+6-:B=$(sY,LkbHa#\'@Q",
			"hvMX,\'4Ty;[a8/{6l~F_V\"}qLI\\!@x(D7bRmUH]W15J%N0BYPkrs&9:$)Zj>u|zwQ=ieC-oGA.#?tfdcO3gp`S+En K2*<",
			"jd!W5[];4\'<C$/&x|rZ(k{>?ghBzIFN}fAK\"#`p_TqtD*1E37XGVs@0nmSe+Y6Qyo-aUu%i8c=H2vJ\\) R:MLb.9,wlO~P",
			"2ThtjEM+!=xXb)7,ZV{*ci3\"8@_l-HS69L>]\\AUF/Q%:qD?1~m(yvO0e\'<#o$p4dnIzKP|`NrkaGg.ufCRB[; sJYwW}5&",
			"vB\\5/zl-9y:Pj|=(R\'7QJI *&CTX\"p0]_3.idcuOefVU#omwNZ`$Fs?L+1Sk<,b)hM4A6[Y%aDrg@~KqEW8t>H};n!2xG{",
			"sFz0Bo@_HfnK>LR}qWXV+D6`Y28=4Cm~G/7-5A\\b9!a#rP.l&M$hc3ijQk;),TvUd<[:I\"u1\'NZSOw]*gxtE{eJp|y (?%",
			"M@,D}|LJyGO8`$*ZqH .j>c~h<d=fimszv[#-53F!+a;NC\'6T91IV?(0x&/{B)w\"]Q\\YUWprk4:ol%g2nE7teRKbAPuS_X",
			".mjY#_0*H<B=Q+FML6]s;r2:e8R}[ic&KA 1w{)vV5d,$u\"~xD/Pg?IyfthO@CzWp%!`N4Z\'3-(o|J9XUE7k\\TlqSb>anG",
			"xVa1\']_GU<X`|\\NgM?LS9{\"jT%s$}y[nvtlefB2RKJW~(/cIDCPow4,>#zm+:5b@06O3Ap8=*7ZFY!H-uEQk; .q)i&rhd",
			"I]Jz7AG@QX.\"%3Lq>METUo{Pp_ |a6<0dYVSv8:b)~W9NK`(r\'4fs&wim\\kReC2hg=HOj$1B*/nxt,;c#y+![?lFuZ-5D}",
			"Rr(Ge6F Hx>q$m&C%M~Tn,:\"o\'tX/*yP.{lZ!YkiVhuw_<KE5a[;}W0gjsz3]@7cI2\\QN?f#4p|vb1OUBD9)=-LJA+d`S8",
			"I~k>y|m};d)-7DZ\"Fe/Y<B:xwojR,Vh]O0Sc[`$sg8GXE!1&Qrzp._W%TNK(=J 3i*2abuHA4C\'?Mv\\Pq{n#56LftUl@9+",
			"~A*>9 WidFN,1KsmwQ)GJM{I4:C%}#Ep(?HB/r;t.&U8o|l[\'Lg\"2hRDyZ5`nbf]qjc0!zS-TkYO<_=76a\\X@$Pe3+xVvu",
			"yYgjf\"5VdHc#uA,W1i+v\'6|@pr{n;DJ!8(btPGaQM.LT3oe?NB/&9>Z`-}02*%x<7lsqz4OS ~E$\\R]KI[:UwC_=h)kXmF",
			"5:iar.{YU7mBZR@-K|2 \"+~`M%8sq4JhPo<_X\\Sg3WC;Tuxz,fvEQ1p9=w}FAI&j/keD0c?)LN6OHV]lGy\'$*>nd[(tb!#\')" };

	/**
	 * Encrypts a string using the same encoding algorithm as the RPC Broker uses.
	 * @param normalText  the text to encode
	 * @param preventEncryptionsContainingCDataSectionBoundaries if true, the returned encrypted strings are guaranteed
	 * not to contain either "]]&gt;" or "&lt;![CDATA[". Otherwise, it is possible a returned encryption may contain
	 * those character sequences.
	 * @return an encrypted (encoded) version of the input string
	 * @throws VistaHashCountLimitExceededException if requested that the method not return a result with CData section
	 * boundaries, and if the algorithm runs up to a count limit (presently 2000 tries) without generating a result
	 * without such boundaries, an exception is thrown.
	 */
	public static String encrypt(
		String normalText,
		boolean preventEncryptionsContainingCDataSectionBoundaries)
		throws VistaKernelHashCountLimitExceededException {

		byte associatorIndex = 0;
		byte identifierIndex = 0;
		byte lead = 0;
		byte trail = 0;
		StringBuffer newString = null;
		int count = 0;

		do {
			// set up associator index
			associatorIndex = (byte) (Math.random() * maxKey);

			// set up identifier index
			identifierIndex = (byte) (Math.random() * maxKey);
			while (identifierIndex == associatorIndex) {
				identifierIndex = (byte) (Math.random() * maxKey);
			}

			lead = (byte) (associatorIndex + 32);
			trail = (byte) (identifierIndex + 32);

			newString = new StringBuffer("");
			for (int index = 0; index < normalText.length(); index++) {
				String substring = normalText.substring(index, index + 1);
				int position = cipherPad[associatorIndex].indexOf(substring);
				newString.append(
					cipherPad[identifierIndex].substring(
						position,
						position + 1));
			}
			count++;
			if (count > countLimit) {
				throw new VistaKernelHashCountLimitExceededException(
					"VistaKernelHash could not produce an encryption within "
						+ countLimit
						+ "tries.");
			}

		}
		while ((preventEncryptionsContainingCDataSectionBoundaries)
			&& ((newString.toString().indexOf(CDATA_BOUNDARY_CLOSE) > 0)
				|| (newString.toString().indexOf(CDATA_BOUNDARY_OPEN) > 0)));

		return (char) lead + newString.toString() + (char) trail;
	}

	/**
	 * Decrypts a string using the same encoding algorithm as the RPC Broker uses.
	 * @param encryptedText  the text to decode
	 * @return a decrypted (decoded) version of the input string.
	 */
	public static String decrypt(String encryptedText) {
		StringBuffer result = new StringBuffer("");
		if (encryptedText.length() > 0) {
			char textChars[] = encryptedText.toCharArray();
			int associatorIndex = textChars[0] - 32;
			int identifierIndex = textChars[textChars.length - 1] - 32;
			for (int i = 1; i < textChars.length - 1; i++) {
				int position = cipherPad[identifierIndex].indexOf(textChars[i]);
				result.append(
					cipherPad[associatorIndex].substring(
						position,
						position + 1));
			}
		}
		return result.toString();
	}

}
