package com.example.app1;

import com.google.common.collect.ImmutableSet;

import org.bouncycastle.asn1.ASN1Boolean;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Enumerated;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DEROctetString;

import java.io.IOException;
import java.math.BigInteger;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;


import com.google.common.io.BaseEncoding;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.Map;
import co.nstant.in.cbor.model.SimpleValue;
import co.nstant.in.cbor.model.SimpleValueType;


public class bl {
    public static int getIntegerFromAsn1(ASN1Encodable asn1Value)
            throws CertificateParsingException {
        if (asn1Value instanceof ASN1Integer) {
            return bigIntegerToInt(((ASN1Integer) asn1Value).getValue());
        } else if (asn1Value instanceof ASN1Enumerated) {
            return bigIntegerToInt(((ASN1Enumerated) asn1Value).getValue());
        } else {
            throw new CertificateParsingException(
                    "Integer value expected, " + asn1Value.getClass().getName() + " found.");
        }
    }

    public static Long getLongFromAsn1(ASN1Encodable asn1Value) throws CertificateParsingException {
        if (asn1Value instanceof ASN1Integer) {
            return bigIntegerToLong(((ASN1Integer) asn1Value).getValue());
        } else {
            throw new CertificateParsingException(
                    "Integer value expected, " + asn1Value.getClass().getName() + " found.");
        }
    }

    public static byte[] getByteArrayFromAsn1(ASN1Encodable asn1Encodable)
            throws CertificateParsingException {
        if (!(asn1Encodable instanceof DEROctetString)) {
            throw new CertificateParsingException("Expected DEROctetString");
        }
        ASN1OctetString derOctectString = (ASN1OctetString) asn1Encodable;
        return derOctectString.getOctets();
    }

    public static ASN1Encodable getAsn1EncodableFromBytes(byte[] bytes)
            throws CertificateParsingException {
        try (ASN1InputStream asn1InputStream = new ASN1InputStream(bytes)) {
            return asn1InputStream.readObject();
        } catch (IOException e) {
            throw new CertificateParsingException("Failed to parse Encodable", e);
        }
    }

    public static ASN1Sequence getAsn1SequenceFromBytes(byte[] bytes)
            throws CertificateParsingException {
        try (ASN1InputStream asn1InputStream = new ASN1InputStream(bytes)) {
            return getAsn1SequenceFromStream(asn1InputStream);
        } catch (IOException e) {
            throw new CertificateParsingException("Failed to parse SEQUENCE", e);
        }
    }

    public static ASN1Sequence getAsn1SequenceFromStream(final ASN1InputStream asn1InputStream)
            throws IOException, CertificateParsingException {
        ASN1Primitive asn1Primitive = asn1InputStream.readObject();
        if (!(asn1Primitive instanceof ASN1OctetString)) {
            throw new CertificateParsingException(
                    "Expected octet stream, found " + asn1Primitive.getClass().getName());
        }
        try (ASN1InputStream seqInputStream = new ASN1InputStream(
                ((ASN1OctetString) asn1Primitive).getOctets())) {
            asn1Primitive = seqInputStream.readObject();
            if (!(asn1Primitive instanceof ASN1Sequence)) {
                throw new CertificateParsingException(
                        "Expected sequence, found " + asn1Primitive.getClass().getName());
            }
            return (ASN1Sequence) asn1Primitive;
        }
    }

    public static Set<Integer> getIntegersFromAsn1Set(ASN1Encodable set)
            throws CertificateParsingException {
        if (!(set instanceof ASN1Set)) {
            throw new CertificateParsingException(
                    "Expected set, found " + set.getClass().getName());
        }

        ImmutableSet.Builder<Integer> builder = ImmutableSet.builder();
        for (Enumeration<?> e = ((ASN1Set) set).getObjects(); e.hasMoreElements();) {
            builder.add(getIntegerFromAsn1((ASN1Integer) e.nextElement()));
        }
        return builder.build();
    }
    /*
    public static String getStringFromAsn1OctetStreamAssumingUTF8(ASN1Encodable encodable)
            throws CertificateParsingException {
        if (!(encodable instanceof ASN1OctetString octetString)) {
            throw new CertificateParsingException(
                    "Expected octet string, found " + encodable.getClass().getName());
        }

        return new String(octetString.getOctets(), StandardCharsets.UTF_8);
    }

    public static String getStringFromASN1PrintableString(ASN1Encodable encodable)
            throws CertificateParsingException {
        if (!(encodable instanceof ASN1PrintableString printableString)) {
            throw new CertificateParsingException(
                    "Expected printable string, found " + encodable.getClass().getName());
        }
        return printableString.getString();
    }
    */
    public static Date getDateFromAsn1(ASN1Primitive value) throws CertificateParsingException {
        return new Date(getLongFromAsn1(value));
    }

    public static boolean getBooleanFromAsn1(ASN1Encodable value)
            throws CertificateParsingException {
        if (!(value instanceof ASN1Boolean booleanValue)) {
            throw new CertificateParsingException(
                    "Expected boolean, found " + value.getClass().getName());
        }
        if (booleanValue.equals(ASN1Boolean.TRUE)) {
            return true;
        } else if (booleanValue.equals((ASN1Boolean.FALSE))) {
            return false;
        }

        throw new CertificateParsingException(
                "DER-encoded boolean values must contain either 0x00 or 0xFF");
    }

    private static int bigIntegerToInt(BigInteger bigInt) throws CertificateParsingException {
        if (bigInt.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) > 0
                || bigInt.compareTo(BigInteger.ZERO) < 0) {
            throw new CertificateParsingException("INTEGER out of bounds");
        }
        return bigInt.intValue();
    }

    private static long bigIntegerToLong(BigInteger bigInt) throws CertificateParsingException {
        if (bigInt.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0
                || bigInt.compareTo(BigInteger.ZERO) < 0) {
            throw new CertificateParsingException("INTEGER out of bounds");
        }
        return bigInt.longValue();
    }



    public static Boolean getBoolean(Map map, DataItem index) {
        SimpleValueType value = ((SimpleValue) map.get(index)).getSimpleValueType();
        if (value != SimpleValueType.TRUE && value != SimpleValueType.FALSE) {
            throw new RuntimeException("Only expecting boolean values for " + index);
        }
        return (value == SimpleValueType.TRUE);
    }

    public static List<Boolean> getBooleanList(Map map, DataItem index) {
        Array array = (Array) map.get(index);
        List<Boolean> result = new ArrayList<>();
        for (DataItem item : array.getDataItems()) {
            SimpleValueType value = ((SimpleValue) item).getSimpleValueType();
            if (value == SimpleValueType.FALSE) {
                result.add(false);
            } else if (value == SimpleValueType.TRUE) {
                result.add(true);
            } else {
                throw new RuntimeException("Map contains more than booleans: " + map);
            }
        }
        return result;
    }

    static final String EAT_OID = "1.3.6.1.4.1.11129.2.1.25";


    public Map getEatExtension(X509Certificate x509Cert)
            throws CertificateParsingException, CborException {
        final Map extension = getEatExtension(x509Cert);
        byte[] attestationExtensionBytes = x509Cert.getExtensionValue(EAT_OID);
        if (attestationExtensionBytes == null || attestationExtensionBytes.length == 0) {
            throw new CertificateParsingException("Did not find extension with OID " + EAT_OID);
        }
        ASN1Encodable asn1 = getAsn1EncodableFromBytes(attestationExtensionBytes);
        byte[] cborBytes = getByteArrayFromAsn1(asn1);
        List<DataItem> cbor = CborDecoder.decode(cborBytes);
        return (Map) cbor.get(0);
    }


    static int eatBootStateTypeToVerifiedBootState(List<Boolean> bootState, Boolean officialBuild) {
        if (bootState.size() != 5) {
            throw new RuntimeException("Boot state map has unexpected size: " + bootState.size());
        }
        if (bootState.get(4)) {
            throw new RuntimeException("debug-permanent-disable must never be true: " + bootState);
        }
        boolean verifiedOrSelfSigned = bootState.get(0);
        if (verifiedOrSelfSigned != bootState.get(1)
                && verifiedOrSelfSigned != bootState.get(2)
                && verifiedOrSelfSigned != bootState.get(3)) {
            throw new RuntimeException("Unexpected boot state: " + bootState);
        }

        if (officialBuild) {
            if (!verifiedOrSelfSigned) {
                throw new AssertionError("Non-verified official build");
            }
            return RootOfTrust.KM_VERIFIED_BOOT_VERIFIED;
        } else {
            return verifiedOrSelfSigned
                    ? RootOfTrust.KM_VERIFIED_BOOT_SELF_SIGNED
                    : RootOfTrust.KM_VERIFIED_BOOT_UNVERIFIED;
        }
    }



}




