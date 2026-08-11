package com.cdss.backend.blockchain;

import org.web3j.codegen.TruffleJsonFunctionWrapperGenerator;

public class GenerateWrapper {
    public static void main(String[] args) throws Exception {
        String jsonPath  = "C:/Users/iamsa/OneDrive - Cardiff Metropolitan University/Desktop/cdss-blockchain-ai/blockchain/build/contracts/PrescriptionRegistry.json";
        String outputDir = "C:/Users/iamsa/OneDrive - Cardiff Metropolitan University/Desktop/cdss-blockchain-ai/backend/src/main/java";
        String packageName = "com.cdss.backend.blockchain";

        TruffleJsonFunctionWrapperGenerator.main(new String[]{
                jsonPath,
                "-o", outputDir,
                "-p", packageName
        });

        System.out.println("✅ Wrapper generated successfully!");
    }
}
