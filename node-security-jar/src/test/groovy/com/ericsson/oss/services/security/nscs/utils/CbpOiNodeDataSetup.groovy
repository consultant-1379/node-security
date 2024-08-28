/*-----------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2020
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.nscs.utils

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject

class CbpOiNodeDataSetup extends NodeDataSetup {

    public static final String OAM_NODE_CREDENTIAL_CERT = "MIIEkwYJKoZIhvcNAQcCoIIEhDCCBIACAQExADALBgkqhkiG9w0BBwGgggRmMIIE YjCCA0qgAwIBAgIIO2rehuYPCScwDQYJKoZIhvcNAQELBQAwTDESMBAGA1UEAwwJ TkVfT0FNX0NBMQswCQYDVQQGEwJTRTERMA8GA1UECgwIRVJJQ1NTT04xFjAUBgNV BAsMDUJVQ0lfRFVBQ19OQU0wHhcNMjExMTI1MDczNjU3WhcNMjMxMTI1MDczNjU3 WjBSMRgwFgYDVQQDDA81RzExNnZEVTAwMS1vYW0xCzAJBgNVBAYTAlNFMREwDwYD VQQKDAhFUklDU1NPTjEWMBQGA1UECwwNQlVDSSBEVUFDIE5BTTCCASIwDQYJKoZI hvcNAQEBBQADggEPADCCAQoCggEBAL/Ssg/+1bAal3/HPBCN++KO/I1t/fh+lflb +zIJ7rlhMjO/fH+KsIA4KY2694G2/XnJfLBstmS4BwGkCQaZdfxVuXFZzyHlGyG0 ueoqzMl1PSknaoG7bDkB3FUUcqyl1r0WGNEgA8NkYwb3+1FUPqzNO4VjfuuptNbE EMs7n9gPTmrXitVMS6jeW/zsRo1jRYiOooHFb38nr1NWGp3ojFV6HfeSaKfLW4ph EbptTZpghLMr09JEZh6qhimlpBqZEpV3vKA3B4krT4VvLYtK20gYfGacbRem14si hdRAa4p/Kr4XflVecrWplAfs/0APpE76vyHSW4YaxVSt7seUbEECAwEAAaOCAUAw ggE8MIHbBgNVHR8EgdMwgdAwYKBeoFyGWmh0dHA6Ly8xOTIuMTY4LjAuMTU1Ojgw OTIvcGtpLWNkcHM/Y2FfbmFtZT1ORV9PQU1fQ0EmY2FfY2VydF9zZXJpYWxudW1i ZXI9MTY3NDQ1ZDQ5ZmFmYWMyMzBsoGqgaIZmaHR0cDovL1syMDAxOjFiNzA6ODJh MToxMDM6OjE4MV06ODA5Mi9wa2ktY2Rwcz9jYV9uYW1lPU5FX09BTV9DQSZjYV9j ZXJ0X3NlcmlhbG51bWJlcj0xNjc0NDVkNDlmYWZhYzIzMB0GA1UdDgQWBBQWhBkn qtHG7oMRhDz1Y9l7kFqHHzAMBgNVHRMBAf8EAjAAMB8GA1UdIwQYMBaAFOCV7VOy Qg43h636UhRYS2eBdOo9MA4GA1UdDwEB/wQEAwIDqDANBgkqhkiG9w0BAQsFAAOC AQEACsY6QMX+QvZcBikOVKaAGnCE2KJpD0/t5X/8LMWWojcOyMfBX/RJ5lCg6jZe r9jzDY82tDmSp2sBeWrNt1lUI3LVESFA4PB97lVd9b3TBmTihqYfToilbspzj7ao TibCMmWiZFXeNJCou6/tZBb9HlC2czrvhvv5x9WIduD15dBXXMt8fsASH9Bm0Aza XjfMD4F+kniECQS8B8upMG/kxvjJ8GSxoYhNrW4AjJSC2AllQliMI+cXz80m3h3e J+sVKxsCXfCDxhGYN14Kx3RaL0KjSIbMddwwVf6JaV45V4bocYFH0RngqCn0havO ULoX+b1xQHwIVE89t6y2Z50vB6EAMQA="
    public static final String OAM_NODE_CREDENTIAL_SUBJECT = "OU=BUCI DUAC NAM,O=ERICSSON,C=SE,CN=5G116vDU001-oam"
    public static final String OAM_NODE_CREDENTIAL_SN = "4281479066735806759"
    public static final String OAM_NODE_CREDENTIAL_ISSUER = "OU=BUCI_DUAC_NAM,O=ERICSSON,C=SE,CN=NE_OAM_CA"

    public static final String ENM_PKI_Root_CA_CERT = "MIIDmQYJKoZIhvcNAQcCoIIDijCCA4YCAQExADALBgkqhkiG9w0BBwGgggNsMIID aDCCAlCgAwIBAgIIOZbzDanAHBkwDQYJKoZIhvcNAQELBQAwUjEYMBYGA1UEAwwP RU5NX1BLSV9Sb290X0NBMREwDwYDVQQKDAhFUklDU1NPTjELMAkGA1UEBhMCU0Ux FjAUBgNVBAsMDUJVQ0lfRFVBQ19OQU0wHhcNMjExMTE4MDE1NTM0WhcNMzExMTE4 MDE1NTM0WjBSMRgwFgYDVQQDDA9FTk1fUEtJX1Jvb3RfQ0ExETAPBgNVBAoMCEVS SUNTU09OMQswCQYDVQQGEwJTRTEWMBQGA1UECwwNQlVDSV9EVUFDX05BTTCCASIw DQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBAKFcdGd8D/t7ADw/Mt4RuyjuEQM/ 6RKTfDxhTPXjgn3ycKToaRMXmpok3ROEOrON0qIwh6XqRsFX/D7+UOKJz3w5uMBr vMiS/3j+WH69WGkVvu9/i/dQC1fkK0vGxjxwBNgH4+2ZQdDFB6+Z1UmBEsG9kX9f 6P7V2AOzPP+nOaAhXCa4VCblg1igV+q/CfDdds68uBNiR8JsluJ7HfLC15fPZ+Ik Q/6Q/uasR9PXsq2RUXmTxbaZJBUvEt9ZIxAVMz9l9mmdMIvrQjhdiUrk3rHsm+hn /3MO3qZjvVeqLdBstZa/4j6ypyh66ksGVs650DBuv+cpE2O6g71hS7qXqr8CAwEA AaNCMEAwHQYDVR0OBBYEFKjG8nZTioZnxI0UYrBqQC5HaL4vMA8GA1UdEwEB/wQF MAMBAf8wDgYDVR0PAQH/BAQDAgEGMA0GCSqGSIb3DQEBCwUAA4IBAQCZmWAFthGh hYbEPfqP/bEXIo91ZByYNX3UzKqv+Kwmd/keGwal20gcK8HKRK2WSLGy4tyxefA3 ucjkiBgaaYOAIO/PrHBNlqDne898r/eeaZBYmy4v5BXDhC3S69QMUMApWU3bWvrq 1sVcPIA+zRFxi5VADgnfkSnxIe71LyllDi9bAzkf1NE8T4hrRQjgZlndVLShpfzX DHGFeyT9x2o9mPeqI6GFvDXBJXvvP51APhPrJlsrxEJzMRAtoiUQ+jxvs2ViOAOs QlBPGWV7FJh8Ch4o7YHdzR/76ea+Rxk+UTQUW9IjWmgmu30g/3j24pNtK+u6XNyb 2aKyPKeIwuzdoQAxAA=="
    public static final String ENM_PKI_Root_CA_SUBJECT = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=ENM_PKI_Root_CA"
    public static final String ENM_PKI_Root_CA_SN = "4149771346676554777"
    public static final String ENM_PKI_Root_CA_ISSUER = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=ENM_PKI_Root_CA"
    public static final String ENM_OAM_CA_CERT = "MIIEtQYJKoZIhvcNAQcCoIIEpjCCBKICAQExADALBgkqhkiG9w0BBwGgggSIMIIE hDCCA2ygAwIBAgIIXmdVpWHS+tUwDQYJKoZIhvcNAQELBQAwWDEeMBwGA1UEAwwV RU5NX0luZnJhc3RydWN0dXJlX0NBMREwDwYDVQQKDAhFUklDU1NPTjELMAkGA1UE BhMCU0UxFjAUBgNVBAsMDUJVQ0lfRFVBQ19OQU0wHhcNMjExMTE4MDE1NTQwWhcN MjkxMTE4MDEyNDQwWjBNMRMwEQYDVQQDDApFTk1fT0FNX0NBMREwDwYDVQQKDAhF UklDU1NPTjELMAkGA1UEBhMCU0UxFjAUBgNVBAsMDUJVQ0lfRFVBQ19OQU0wggEi MA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCaHmHuRS7HAtcPr6oMjt3NnSIf UIKHGYA91Nf+pSNeH7ooiSQGB4d4S09gvLDPXAkAo87UePociZ5nh7TN0pRO9UTK YgdmTXBtPoZKdHp87EWwkjh9QIUUfhxQWTWkgrE5+JN05950UXDiok0e5Zv9Xnx4 Hz/EONjM/qv9cMmC2IhAFSqesYR4AUYAR26YiKDCnQ2XeAtUOe+2qiwwiKlDY+fp 40vyrEsDLPQImYq7unwwfkLNT6G45rwqeByykrkNghFKFDkVsiCaydKlTLrS2b7K 5NQDq7LIBRBQ2P6dfTKCw8bll4khyeyLyUZMezeHfAcogoYXrz1BV+ncqY4xAgMB AAGjggFbMIIBVzCB8wYDVR0fBIHrMIHoMGygaqBohmZodHRwOi8vMTkyLjE2OC4w LjE1NTo4MDkyL3BraS1jZHBzP2NhX25hbWU9RU5NX0luZnJhc3RydWN0dXJlX0NB JmNhX2NlcnRfc2VyaWFsbnVtYmVyPTdhMmFhZTM3NTBmOGI2ODEweKB2oHSGcmh0 dHA6Ly9bMjAwMToxYjcwOjgyYTE6MTAzOjoxODFdOjgwOTIvcGtpLWNkcHM/Y2Ff bmFtZT1FTk1fSW5mcmFzdHJ1Y3R1cmVfQ0EmY2FfY2VydF9zZXJpYWxudW1iZXI9 N2EyYWFlMzc1MGY4YjY4MTAdBgNVHQ4EFgQU2kXT0pFliEbB3+nSuJore4Egx0cw DwYDVR0TAQH/BAUwAwEB/zAfBgNVHSMEGDAWgBSsvd6oZMfMMwHyBJ8AOHJ62t77 zDAOBgNVHQ8BAf8EBAMCAQYwDQYJKoZIhvcNAQELBQADggEBAEgeFfr7DKe5MjwV MTFBQphSr8P2GN5WpJxoE3bpImJVsTiTmjOCZVOoNwzTJYYpf/b0scX0ezi7iW5I 9MEJXQoklDI/3E3Sw+3+Vv0aOAQbFlYrRajH8m10qjvyWO0su9gTlMc6vUZ4eeau /xVcM53MhqdhTU6disRznP7Re+PBGLH/ZDAgdwt64ABsvcD7r+MTRWTHYIG3TdbE p/gvqk6pM21txTQ2AdwyziuZfd22l6WgoESLXLJGerSENB2IQ/UBHtHlH2xkRZDV OHZSsFHh9XUSZfL/Cp9FkOVFzddbbRbWIKOLGpkW5TsnIbjhO5BL2fGpMTwq5j3b 0UuGLE+hADEA"
    public static final String ENM_OAM_CA_SUBJECT = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=ENM_OAM_CA"
    public static final String ENM_OAM_CA_SN = "6802499930965605077"
    public static final String ENM_OAM_CA_ISSUER = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=ENM_Infrastructure_CA"
    public static final String ENM_Infrastructure_CA_CERT = "MIIErgYJKoZIhvcNAQcCoIIEnzCCBJsCAQExADALBgkqhkiG9w0BBwGgggSBMIIE fTCCA2WgAwIBAgIIeiquN1D4toEwDQYJKoZIhvcNAQELBQAwUjEYMBYGA1UEAwwP RU5NX1BLSV9Sb290X0NBMREwDwYDVQQKDAhFUklDU1NPTjELMAkGA1UEBhMCU0Ux FjAUBgNVBAsMDUJVQ0lfRFVBQ19OQU0wHhcNMjExMTE4MDE1NTM4WhcNMjkxMTE4 MDE1NTM4WjBYMR4wHAYDVQQDDBVFTk1fSW5mcmFzdHJ1Y3R1cmVfQ0ExETAPBgNV BAoMCEVSSUNTU09OMQswCQYDVQQGEwJTRTEWMBQGA1UECwwNQlVDSV9EVUFDX05B TTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBALnVOV5fqUhiu/UjYTtz V0OEZb7kznUuDkanNJqI7tbOxD+kM25iLM0r/c4mMAUb51pZOzwgXAk64EtvcNS4 DiefFzI8XPNKegpr9W/hBd0i6Ip+fWQ/VR0q+NZuOyyr0z8s3JAP5pUHePr6zsHe gEKo9RoBTOlCu0ymm76NUcqhIiKwzLv5g2gN4VZl5wtXI0DClR3hAH8y1lXexC3H +9/P+DRtA+A4Qy76v0bh0NH3gXkZtrN2FPBW5cjV1pBBJNXSBkdlZWcCkxT7P05z nRkdscf1O8inmZIRpAaBKD0W+I5U/52tB5AvqF9FfveOzJ9DLWYspogQrdNKU5LS /cMCAwEAAaOCAU8wggFLMIHnBgNVHR8Egd8wgdwwZqBkoGKGYGh0dHA6Ly8xOTIu MTY4LjAuMTU1OjgwOTIvcGtpLWNkcHM/Y2FfbmFtZT1FTk1fUEtJX1Jvb3RfQ0Em Y2FfY2VydF9zZXJpYWxudW1iZXI9Mzk5NmYzMGRhOWMwMWMxOTByoHCgboZsaHR0 cDovL1syMDAxOjFiNzA6ODJhMToxMDM6OjE4MV06ODA5Mi9wa2ktY2Rwcz9jYV9u YW1lPUVOTV9QS0lfUm9vdF9DQSZjYV9jZXJ0X3NlcmlhbG51bWJlcj0zOTk2ZjMw ZGE5YzAxYzE5MB0GA1UdDgQWBBSsvd6oZMfMMwHyBJ8AOHJ62t77zDAPBgNVHRMB Af8EBTADAQH/MB8GA1UdIwQYMBaAFKjG8nZTioZnxI0UYrBqQC5HaL4vMA4GA1Ud DwEB/wQEAwIBBjANBgkqhkiG9w0BAQsFAAOCAQEADdE4duvL9gtmE1Gl2lEskk6U +eHkBBxPPy1Xriv8a52I+NzPQJJ7strvDeJsdQm+ozJE6RD5AjdDNdfoDtMHXBrk OXprWPHEZrzMe2tW+U+xdA33A67qat9tvNrG4tpSKj1706j4qROldx7vDTVlsHPd +auCL7LFuzDDhxnrirtRKKdNIEaMf9DwhbC2VVWT3/Hy9l/0Pde2GQ/UOcQJ7C5L SXX3QjMjcBlDV8wOtcqoNw+zIE3+USsOF2EMU6HkhIhSiEq2lRnwo7o/hySdEY80 L1Em3MCHIe/PtPHeTRCzAAObwlzI3qkOQvZ4QSPlcuhKDe8+ptirYNBRjSUQQqEA MQA="
    public static final String ENM_Infrastructure_CA_SUBJECT = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=ENM_Infrastructure_CA"
    public static final String ENM_Infrastructure_CA_SN = "8803039974253966977"
    public static final String ENM_Infrastructure_CA_ISSUER = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=ENM_PKI_Root_CA"
    public static final String NE_OAM_CA_CERT = "MIIEogYJKoZIhvcNAQcCoIIEkzCCBI8CAQExADALBgkqhkiG9w0BBwGgggR1MIIE cTCCA1mgAwIBAgIIBeiz2DEY8d0wDQYJKoZIhvcNAQELBQAwUjEYMBYGA1UEAwwP RU5NX1BLSV9Sb290X0NBMREwDwYDVQQKDAhFUklDU1NPTjELMAkGA1UEBhMCU0Ux FjAUBgNVBAsMDUJVQ0lfRFVBQ19OQU0wHhcNMjExMTE4MDE1NTM3WhcNMjkxMTE4 MDE1NTM3WjBMMRIwEAYDVQQDDAlORV9PQU1fQ0ExETAPBgNVBAoMCEVSSUNTU09O MQswCQYDVQQGEwJTRTEWMBQGA1UECwwNQlVDSV9EVUFDX05BTTCCASIwDQYJKoZI hvcNAQEBBQADggEPADCCAQoCggEBAOgoGMAcPebLF6TBLel6vLsLRIZtd2At7xm5 V99cttNwhnEEVcOkGBY2Dn9YzAo9yrOLalayBVI5MsRAJom9A2aoHMeYa1OGodAO maOBgKXMZ23fiPEBIas3hcUcQqA4moevFuMHY3aCDXZ6hR5RDNd/h8HnaH+pXIil UA7iont+i9M/FiF3Ak2V37nOTX5b+x4C6CwCdTVsv62m5SsDKmlZpiwVR1D2ERXJ kDG0evZT7G3fVHeXUamb1d4HQ88DPE+/CdIkxi4ULt4KTJJEblGFcSwVid0Qlaq7 Wet/z28sWXuDBtCDmwuSeH66ncvS0GoMI4I4ayowFemQUlcEN7MCAwEAAaOCAU8w ggFLMIHnBgNVHR8Egd8wgdwwZqBkoGKGYGh0dHA6Ly8xOTIuMTY4LjAuMTU1Ojgw OTIvcGtpLWNkcHM/Y2FfbmFtZT1FTk1fUEtJX1Jvb3RfQ0EmY2FfY2VydF9zZXJp YWxudW1iZXI9Mzk5NmYzMGRhOWMwMWMxOTByoHCgboZsaHR0cDovL1syMDAxOjFi NzA6ODJhMToxMDM6OjE4MV06ODA5Mi9wa2ktY2Rwcz9jYV9uYW1lPUVOTV9QS0lf Um9vdF9DQSZjYV9jZXJ0X3NlcmlhbG51bWJlcj0zOTk2ZjMwZGE5YzAxYzE5MB0G A1UdDgQWBBQ8eoExM7AZF4AIIAvjsv5COdHGnDAPBgNVHRMBAf8EBTADAQH/MB8G A1UdIwQYMBaAFKjG8nZTioZnxI0UYrBqQC5HaL4vMA4GA1UdDwEB/wQEAwIBBjAN BgkqhkiG9w0BAQsFAAOCAQEAXF6GQ/Q7kOdRBtYDfekU7Uv+tgwfh8MVHmMuTj0Y lTY+qNOGQYtAewtGBxUwqffs60H6xHZ49CLGTO3ltSMQR95ueI34gi1s48/o5Kih K4i1ODXLjE67VSA8q1EVfE6R3ij3tC12bwngs5dEF25G31FpOnBGWRWUwbr03LmN 9354ijgo7t4RjEW0iHeg+DRftfmmJDiG4QUWFjH+ZFGuA0PvzB0wObaV4VkkZGHW qkaHuTwi/hFg8EsuGNnbIGdYcsLE4/O8dgH3fKmpxsi1TmfOAMHWeK6sogAH5jDf gU4k0GQ3VQe/5gQ3Lc66BxJWaKwOOhsjZvDQI4scgT/8ZaEAMQA="
    public static final String NE_OAM_CA_SUBJECT = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=NE_OAM_CA"
    public static final String NE_OAM_CA_SN = "425787905904538077"
    public static final String NE_OAM_CA_ISSUER = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=ENM_PKI_Root_CA"
    public static final String ENM_E_mail_CA_CERT = "MIIEpgYJKoZIhvcNAQcCoIIElzCCBJMCAQExADALBgkqhkiG9w0BBwGgggR5MIIE dTCCA12gAwIBAgIIKM37GZUnobEwDQYJKoZIhvcNAQELBQAwUjEYMBYGA1UEAwwP RU5NX1BLSV9Sb290X0NBMREwDwYDVQQKDAhFUklDU1NPTjELMAkGA1UEBhMCU0Ux FjAUBgNVBAsMDUJVQ0lfRFVBQ19OQU0wHhcNMjExMTE4MDE1NTM4WhcNMjkxMTE4 MDE1NTM4WjBQMRYwFAYDVQQDDA1FTk1fRS1tYWlsX0NBMREwDwYDVQQKDAhFUklD U1NPTjELMAkGA1UEBhMCU0UxFjAUBgNVBAsMDUJVQ0lfRFVBQ19OQU0wggEiMA0G CSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCeXkSqSlo1A/MXRMRhD6B2YiDoYnDE tSMJaiviesrWsStiftEtDI6C/X1zv48OB0OWCBKi3o9sViInuquMU3PK+SXSbNtz OKVhpsbcB56YDNHBfahSzGGPAlyK+xp3U4Y8Z8/5I9Ou+4BY4Ht4QfwhtUOkpJu/ AlVeCGcyuWxF2W3h9u/HzcDrK2Hmtx9wJtt0QGicALNEMxtYfRaYzLJW8dTJ5rGp k3dW1wrATSFzkdh1NWvRahrZWd+0vB8binNijvxPgzFSNdKmILsayeDF//zFF1BI 7bOnYOhG3hamRBieN3WShIdN+b/TlXY2k7bZKthNgdx9cg5Ck0mn2O7LAgMBAAGj ggFPMIIBSzCB5wYDVR0fBIHfMIHcMGagZKBihmBodHRwOi8vMTkyLjE2OC4wLjE1 NTo4MDkyL3BraS1jZHBzP2NhX25hbWU9RU5NX1BLSV9Sb290X0NBJmNhX2NlcnRf c2VyaWFsbnVtYmVyPTM5OTZmMzBkYTljMDFjMTkwcqBwoG6GbGh0dHA6Ly9bMjAw MToxYjcwOjgyYTE6MTAzOjoxODFdOjgwOTIvcGtpLWNkcHM/Y2FfbmFtZT1FTk1f UEtJX1Jvb3RfQ0EmY2FfY2VydF9zZXJpYWxudW1iZXI9Mzk5NmYzMGRhOWMwMWMx OTAdBgNVHQ4EFgQUvpW3FbpSFxHc0nV2Cyts2GKz9EUwDwYDVR0TAQH/BAUwAwEB /zAfBgNVHSMEGDAWgBSoxvJ2U4qGZ8SNFGKwakAuR2i+LzAOBgNVHQ8BAf8EBAMC AQYwDQYJKoZIhvcNAQELBQADggEBAIDJqkVFqVQbF3CnqGmduagzja7aNwDnOMgm 3P2+Wp97JVdGhK13tHB3iOTlHMJR7bggSeM4o5ANLdDYwURKT0PIQWBKYm3hXtGd 7sNU5OEAebWmRU1dHjkRkD40hHpP/4JYAWtZEqFlhspFxPReuLEJK/wMD53HU9jM fUuuIwal9ZKCTqn3spRk7/H2TBPxLpK6bp1Y9KYxr8l/3aZs29pYxaKCgcRTrXvs TYbb111LLlV4teI4Nic1kc0Zcq7AU8SaLJzQ4hIECxT5nTIhRkcJA08FKJ7X3t3p HRJYeERlu5UFtfRQRm6TRcRAM6u1t5NM/Z/en6GCm9dbuzEYWk6hADEA"
    public static final String ENM_E_mail_CA_SUBJECT = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=ENM_E-mail_CA"
    public static final String ENM_E_mail_CA_SN = "2940282219037958577"
    public static final String ENM_E_mail_CA_ISSUER = "OU=BUCI_DUAC_NAM, C=SE, O=ERICSSON, CN=ENM_PKI_Root_CA"

    private ManagedObject keystore
    private ManagedObject keystoreCmp
    private ManagedObject certificateAuthorities
    private ManagedObject certificateAuthority
    private ManagedObject cmpServerGroups
    private ManagedObject cmpServerGroup
    private ManagedObject cmpServer
    private ManagedObject asymmetricKeys
    private ManagedObject asymmetricKeysCmp
    private ManagedObject asymmetricKey
    private ManagedObject asymmetricKeyCmp
    private Map<String, ManagedObject> nodeCredentials
    private ManagedObject truststore
    private Map<String, ManagedObject> trustCategories
    private ManagedObject system
    private ManagedObject systemLdap
    private ManagedObject security
    private ManagedObject tls
    private ManagedObject simpleAuthenticated
    private Map<String, ManagedObject> ldapServers
    private Map<String, ManagedObject> ldapServersTcp
    private ManagedObject authentication
    private ManagedObject user
    private ManagedObject authorizedKey
    private platform = "CBP-OI"

    def createNodeWithMeContext(String neType, String ossModelIdentity, String nodeName) {
        createNodeWithMeContext(neType, ossModelIdentity, nodeName, platform)
    }

    def createNodeWithManagedElement(String neType, String ossModelIdentity, String nodeName) {
        createNodeWithManagedElement(neType, ossModelIdentity, nodeName, platform, getTopNs(neType))
    }

    def createManagedElementUnderMeContext(String neType, String nodeName) {
        createManagedElementUnderMeContext(neType, nodeName, getTopNs(neType))
    }

    def createKeystoreUnderManagedElement() {
        keystore = runtimeConfigurableDps.addManagedObject()
                .parent(managedElement)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-keystore")
                .type("keystore")
                .name("1")
                .build()
    }

    def createKeystoreUnderMeContext() {
        keystore = runtimeConfigurableDps.addManagedObject()
                .parent(meContext)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-keystore")
                .type("keystore")
                .name("1")
                .build()
    }

    def createCmpUnderKeystore() {
        keystoreCmp = runtimeConfigurableDps.addManagedObject()
                .parent(keystore)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext")
                .type(KEYSTORE_CMP_SCOPED_TYPE)
                .name("1")
                .build()
    }

    def createCertificateAuthoritiesUnderCmp() {
        certificateAuthorities = runtimeConfigurableDps.addManagedObject()
                .parent(keystoreCmp)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext")
                .type("certificate-authorities")
                .name("1")
                .build()
    }

    def createCertificateAuthorityUnderCertificateAuthorities(String name) {
        certificateAuthority = runtimeConfigurableDps.addManagedObject()
                .parent(certificateAuthorities)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext")
                .type("certificate-authority")
                .name(name)
                .addAttribute("name", name)
                .build()
    }

    def createCmpServerGroupsUnderCmp() {
        cmpServerGroups = runtimeConfigurableDps.addManagedObject()
                .parent(keystoreCmp)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext")
                .type("cmp-server-groups")
                .name("1")
                .build()
    }

    def createCmpServerGroupUnderCmpServerGroups(String name) {
        if (name != null) {
            cmpServerGroup = runtimeConfigurableDps.addManagedObject()
                    .parent(cmpServerGroups)
                    .namespace("urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext")
                    .type("cmp-server-group")
                    .name(name)
                    .build()
        } else {
            cmpServerGroup = null
        }
    }

    def createCmpServerUnderCmpServerGroup() {
        cmpServer = runtimeConfigurableDps.addManagedObject()
                .parent(cmpServerGroup)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext")
                .type("cmp-server")
                .name("1")
                .build()
    }

    def createAsymmetricKeysUnderKeystore() {
        asymmetricKeys = runtimeConfigurableDps.addManagedObject()
                .parent(keystore)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-keystore")
                .type("asymmetric-keys")
                .name("1")
                .build()
        nodeCredentials = [:]
    }

    def createAsymmetricKeysCmpUnderAsymmetricKeys() {
        asymmetricKeysCmp = runtimeConfigurableDps.addManagedObject()
                .parent(asymmetricKeys)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext")
                .type(ASYMMETRIC_KEYS_CMP_SCOPED_TYPE)
                .name("1")
                .build()
    }

    def createAsymmetricKeyUnderAsymmetricKeys(String asymmetricKeyName) {
        asymmetricKey = runtimeConfigurableDps.addManagedObject()
                .parent(asymmetricKeys)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-keystore")
                .type("asymmetric-key")
                .name(asymmetricKeyName)
                .build()
        nodeCredentials.put(asymmetricKeyName, asymmetricKey)
    }

    def createAsymmetricKeyCmpUnderAsymmetricKey(String renewalMode, String cmpServerGroup, String trustedCerts) {
        asymmetricKeyCmp = runtimeConfigurableDps.addManagedObject()
                .parent(asymmetricKey)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-keystore-ext")
                .type(ASYMMETRIC_KEY_CMP_SCOPED_TYPE)
                .name("1")
                .addAttribute("renewal-mode", renewalMode)
                .addAttribute("cmp-server-group", cmpServerGroup)
                .addAttribute("trusted-certs", trustedCerts)
                .build()
    }

    def createCertificatesUnderAsymmetricKey(String certName) {
        def currAsymmetricKey = nodeCredentials[certName]
        if (currAsymmetricKey != null) {
            def asymmetricKeyCertificates = runtimeConfigurableDps.addManagedObject()
                    .parent(currAsymmetricKey)
                    .namespace("urn:ietf:params:xml:ns:yang:ietf-keystore")
                    .type("certificates")
                    .name("1")
                    .build()
        }
    }

    def createCertificatesHierarchyUnderAsymmetricKey(String certName, String cert) {
        def currAsymmetricKey = nodeCredentials[certName]
        if (currAsymmetricKey != null) {
            def asymmetricKeyCertificates = runtimeConfigurableDps.addManagedObject()
                    .parent(currAsymmetricKey)
                    .namespace("urn:ietf:params:xml:ns:yang:ietf-keystore")
                    .type("certificates")
                    .name("1")
                    .build()
            def certificate = runtimeConfigurableDps.addManagedObject()
                    .parent(asymmetricKeyCertificates)
                    .namespace("urn:ietf:params:xml:ns:yang:ietf-keystore")
                    .type("certificate")
                    .name(certName)
                    .addAttribute("cert", cert)
                    .build()
        }
    }

    def issueOamCertificateUnderOamNodeCredential() {
        def currAsymmetricKey = nodeCredentials["oamNodeCredential"]
        if (currAsymmetricKey == null) {
            createAsymmetricKeyUnderAsymmetricKeys("oamNodeCredential")
        }
        createCertificatesHierarchyUnderAsymmetricKey("oamNodeCredential", OAM_NODE_CREDENTIAL_CERT)
    }

    def createTruststoreUnderManagedElement() {
        truststore = runtimeConfigurableDps.addManagedObject()
                .parent(managedElement)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-truststore")
                .type("truststore")
                .name("1")
                .build()
        trustCategories = [:]
    }

    def createTruststoreUnderMeContext() {
        truststore = runtimeConfigurableDps.addManagedObject()
                .parent(meContext)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-truststore")
                .type("truststore")
                .name("1")
                .build()
        trustCategories = [:]
    }

    def createCertificatesUnderTruststore(String name) {
        if (name != null) {
            def truststoreCertificates = runtimeConfigurableDps.addManagedObject()
                    .parent(truststore)
                    .namespace("urn:ietf:params:xml:ns:yang:ietf-truststore")
                    .type("certificates")
                    .name(name)
                    .build()
            trustCategories.put(name, truststoreCertificates)
        }
    }

    def createCertificateUnderTruststoreCertificates(String certificatesName, String name, String cert) {
        def truststoreCertificates = trustCategories[certificatesName]
        if (truststoreCertificates != null) {
            if (name != null) {
                runtimeConfigurableDps.addManagedObject()
                        .parent(truststoreCertificates)
                        .namespace("urn:ietf:params:xml:ns:yang:ietf-truststore")
                        .type("certificate")
                        .name(name)
                        .addAttribute("cert", cert)
                        .build()
            }
        }
    }

    def distributeOamCertificatesUnderOamTrustCategory(String trustCategory) {
        def truststoreCertificates = trustCategories[trustCategory]
        if (truststoreCertificates == null) {
            createCertificatesUnderTruststore(trustCategory)
        }
        createCertificateUnderTruststoreCertificates(trustCategory, "ENM_PKI_Root_CA-1", ENM_PKI_Root_CA_CERT)
        createCertificateUnderTruststoreCertificates(trustCategory, "ENM_OAM_CA-1", ENM_OAM_CA_CERT)
        createCertificateUnderTruststoreCertificates(trustCategory, "ENM_Infrastructure_CA-1", ENM_Infrastructure_CA_CERT)
        createCertificateUnderTruststoreCertificates(trustCategory, "NE_OAM_CA-1", NE_OAM_CA_CERT)
    }

    def distributeOamCertificatesUnderOamTrustCategories() {
        distributeOamCertificatesUnderOamTrustCategory("oamTrustCategory")
        distributeOamCertificatesUnderOamTrustCategory("oamCmpCaTrustCategory")
    }

    def createSystemUnderManagedElement() {
        system = runtimeConfigurableDps.addManagedObject()
                .parent(managedElement)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-system")
                .type("system")
                .name("1")
                .build()
    }

    def createSystemUnderMeContext() {
        system = runtimeConfigurableDps.addManagedObject()
                .parent(meContext)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-system")
                .type("system")
                .name("1")
                .build()
    }

    def createLdapUnderSystem(String name) {
        systemLdap = runtimeConfigurableDps.addManagedObject()
                .parent(system)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-system-ext")
                .type(SYSTEM_LDAP_SCOPED_TYPE)
                .name(name)
                .build()
        ldapServers = [:]
        ldapServersTcp = [:]
    }

    def createSecurityUnderSystemLdap(String name, String baseDn) {
        security = runtimeConfigurableDps.addManagedObject()
                .parent(systemLdap)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-system-ext")
                .type("security")
                .name(name)
                .addAttribute("user-base-dn", baseDn)
                .build()
    }

    def createSimpleAuthenticatedUnderSecurity(String name, String bindDn, String bindCrd) {
        security = runtimeConfigurableDps.addManagedObject()
                .parent(security)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-system-ext")
                .type("simple-authenticated")
                .name(name)
                .addAttribute("bind-dn", bindDn)
                .addAttribute("bind-password", bindCrd)
                .build()
    }

    def createTlsUnderSecurity(String name) {
        security = runtimeConfigurableDps.addManagedObject()
                .parent(security)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-system-ext")
                .type("tls")
                .name(name)
                .build()
    }

    def createServerUnderSystemLdap(String name) {
        def server = runtimeConfigurableDps.addManagedObject()
                .parent(systemLdap)
                .namespace("urn:rdns:com:ericsson:oammodel:ericsson-system-ext")
                .type("server")
                .name(name)
                .addAttribute("name", name)
                .build()
        ldapServers.put(name, server)
    }

    def createTcpUnderServer(String serverName, String name, String address) {
        def server = ldapServers[serverName]
        if (server != null) {
            def tcp = runtimeConfigurableDps.addManagedObject()
                    .parent(server)
                    .namespace("urn:rdns:com:ericsson:oammodel:ericsson-system-ext")
                    .type("tcp")
                    .name(name)
                    .addAttribute("address", address)
                    .build()
            ldapServersTcp.put(serverName, tcp)
        }
    }

    def createLdapsUnderTcp(String serverName, String name, Integer port) {
        def tcp = ldapServersTcp[serverName]
        if (tcp != null) {
            runtimeConfigurableDps.addManagedObject()
                    .parent(tcp)
                    .namespace("urn:rdns:com:ericsson:oammodel:ericsson-system-ext")
                    .type("ldaps")
                    .name(name)
                    .addAttribute("port", port)
                    .build()
        }
    }

    def createTcpLdapUnderTcp(String serverName, String name, Integer port) {
        def tcp = ldapServersTcp[serverName]
        if (tcp != null) {
            runtimeConfigurableDps.addManagedObject()
                    .parent(tcp)
                    .namespace("urn:rdns:com:ericsson:oammodel:ericsson-system-ext")
                    .type(TCP_LDAP_SCOPED_TYPE)
                    .name(name)
                    .addAttribute("port", port)
                    .build()
        }
    }

    def createAuthenticationUnderSystem() {
        authentication = runtimeConfigurableDps.addManagedObject()
                .parent(system)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-system")
                .type("authentication")
                .name("1")
                .build()
    }

    def createUserUnderAuthentication(String name) {
        user = runtimeConfigurableDps.addManagedObject()
                .parent(authentication)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-system")
                .type("user")
                .name(name)
                .build()
    }

    def createAuthorizedKeyUnderUser(String name, String algorithm, String comment, String keyData) {
        authorizedKey = runtimeConfigurableDps.addManagedObject()
                .parent(user)
                .namespace("urn:ietf:params:xml:ns:yang:ietf-system")
                .type("authorized-key")
                .name(name)
                .addAttribute("name", name)
                .addAttribute("algorithm", algorithm)
                .addAttribute("comment", comment)
                .addAttribute("key-data", keyData)
                .build()
    }

    private String getTopNs(String neType) {
        if (VDU_TARGET_TYPE == neType) {
            return VDU_TOP_NS
        }
        return null
    }
}
