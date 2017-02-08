package org.cocktail.rbo;

import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwk.VerificationJwkSelector;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.lang.JoseException;
import org.junit.Test;

public class CasTestWithJoseLib {
    
    private String jwtGenereParCAS = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImNhcyJ9.eyJqdGkiOiI5YjU3Mzk4ZS1iZDUyLTQ0ZjItOTMzMy1jODI4NjRiMWFhYTEiLCJpc3MiOiJodHRwczovL2Nhcy5jb2NrdGFpbC5mcjo4NDQzL2Nhcy9vaWRjIiwiYXVkIjoic2VydmltcGxpY2l0IiwiZXhwIjoxNDg2NTkzMTYwLCJpYXQiOjE0ODY1ODU5NjAsIm5iZiI6MTQ4NjU4NTY2MCwic3ViIjoiY2FzdXNlciIsInN0YXRlIjoiYWYwaWZqc2xka2oiLCJub25jZSI6Im4tMFM2X1d6QTJNaiIsImF0X2hhc2giOiI1UUZkaFQyaU9HSlhXTlFnTWZ2OENnPT0iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJjYXN1c2VyIn0.oRnG3Ks7KNf51i8h9Jh_XWnFu1aqS4jHt2hscAF9cotEoJFgvJBqyrKpXvHe0432MqKNDEZaaL5D3kmgY29m3Nz8gwq-0mbXvJJMzoNduZYHpz9IGwGJdNz_ADrbQcRLl41740ZtdL0rXZBx7rp3iKMe8tlQ6oJs-MmFCK9TmBc_JSpJflvcByfUiPil6Fkbmkk9HpHgsjlDlgXLRGNRzQby2TKdGv4WfvyArHepIkYy8MSkLWVpyOHv2LADJneAJfGkZwQO_MDDFMUueqjdvXuEC9__QeKvi4DFIYqmxqI0MaFZKlAXdGpmzhHmW-Ip4scLC-nYQPY8xHEiPsajeg";
    private String e = "AQAB";
    private String n = "7Fm8J-U_EubC4-Ts7h5ZojoFk2H9cAlt9u5ibdiTiBEPjYp1SJJEnfn58ZihCxFl4zksQ7mp00sG5BBc-g2r2InwQ9RDwKHeZEPLolsA7YJErU9dlwtr_pnAMJrgxoqVaBVWd1eovh_3wZ3ijmQTIc1ecRIGNuI4Jddzv1XH03igIe36UJTvVjIWcrNk9phbMyRCJKJzNS9iB3L58jdRQ6o0WnUAdcttku1H_ZlvhY7fN6jM3NdcGiQrX53HO9dwsXaastor1fnv8F10cIaKlHEK4jAgeJCPp1BDdZFdsTXoW5Y8psEYzp9Qe7nWSF96JegPhwGVFP8Lotd1BMF0Zw";
    
    private String jwks = //correspond au contenu de https://cas.cocktail.fr:8443/cas/oidc/jwks
        "{" + 
            "\"keys\": [" +
                "{" +
                    "\"kty\": \"RSA\"," +
                    "\"kid\": \"cas\"," +
                    "\"use\": \"sig\"," +
                    "\"alg\": \"RS256\"," +
                    "\"n\": \"" + n +"\"," +
                    "\"e\": \"" + e + "\"" +
                "}" +
            "]" +
        "}";
    
    @Test
    public void parseJWT() throws JoseException, MalformedClaimException {
        //on crée le jws à partir de ce que CAS nous donne
        JsonWebSignature jws = new JsonWebSignature();
        jws.setCompactSerialization(jwtGenereParCAS);
        //on prends la liste des clés publiques gérées par CAS
        JsonWebKeySet listeClesJwks = new JsonWebKeySet(jwks);
        //on cherche dans cette liste la clé avec laquelle à été signé notre jws
        VerificationJwkSelector jwkSelector = new VerificationJwkSelector();
        JsonWebKey cleJwk = jwkSelector.select(jws, listeClesJwks.getJsonWebKeys());
        if (cleJwk == null) {
            throw new RuntimeException("jwk non trouvé");
        }
        
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setMaxFutureValidityInMinutes(300) // but the  expiration time can't be too crazy
                .setAllowedClockSkewInSeconds(30) // allow some leeway in validating time based claims to account for clock skew
                .setRequireSubject() // the JWT must have a subject claim
                .setExpectedIssuer("https://cas.cocktail.fr:8443/cas/oidc") // whom the JWT needs to have been issued by
                .setExpectedAudience("servimplicit") // to whom the JWT is intended for
                .setVerificationKey(cleJwk.getKey()) // verify the signature with the public key
                .build(); // create the JwtConsumer instance

        try
        {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwtGenereParCAS);
            System.out.println("JWT validation succeeded! " + jwtClaims);
            System.out.println("USER --> " + jwtClaims.getSubject());
        }
        catch (InvalidJwtException e)
        {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            // Hopefully with meaningful explanations(s) about what went wrong.
            System.out.println("Invalid JWT! " + e);
            throw new RuntimeException("Invalid JWT!");
        }        
    }


}
