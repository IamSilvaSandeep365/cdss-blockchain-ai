package com.cdss.backend.service;

import com.cdss.backend.blockchain.PrescriptionRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import jakarta.annotation.PostConstruct;
import java.math.BigInteger;
import org.web3j.tx.gas.StaticGasProvider;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

@Service
@Slf4j
public class BlockchainService {

    @Value("${blockchain.ganache.url}")
    private String ganacheUrl;

    @Value("${blockchain.contract.address}")
    private String contractAddress;

    @Value("${blockchain.wallet.privatekey}")
    private String privateKey;

    private Web3j               web3j;
    private Credentials         credentials;
    private PrescriptionRegistry contract;

    // ============================================================
    // Initialize connection on startup
    // ============================================================
    @PostConstruct
    public void init() {
        try {
            web3j       = Web3j.build(new HttpService(ganacheUrl));
            credentials = Credentials.create(privateKey);

            ContractGasProvider gasProvider = new StaticGasProvider(
                    BigInteger.valueOf(20_000_000_000L),   // gas price: 20 gwei
                    BigInteger.valueOf(6_000_000L)          // gas limit: 6 million (under Ganache's ~6.7M block limit)
            );

            contract = PrescriptionRegistry.load(
                    contractAddress, web3j, credentials, gasProvider);

            log.info("✅ Blockchain connected!");
            log.info("   Ganache URL      : {}", ganacheUrl);
            log.info("   Contract Address : {}", contractAddress);
            log.info("   Wallet Address   : {}", credentials.getAddress());

        } catch (Exception e) {
            log.error("❌ Blockchain connection failed: {}", e.getMessage());
        }
    }

    // ============================================================
    // Store prescription hash on blockchain
    // ============================================================
    public String storePrescription(String prescriptionId, String recordHash) {
        try {
            // Convert hex hash string to bytes32
            byte[] hashBytes = hexStringToBytes32(recordHash);

            // Call smart contract function
            var receipt = contract.storePrescriptionHash(
                    prescriptionId, hashBytes).send();

            String txHash = receipt.getTransactionHash();
            log.info("✅ Prescription stored on blockchain!");
            log.info("   Prescription ID : {}", prescriptionId);
            log.info("   TX Hash         : {}", txHash);

            return txHash;

        } catch (Exception e) {
            log.error("❌ Failed to store on blockchain: {}", e.getMessage());
            throw new RuntimeException("Blockchain storage failed: " + e.getMessage());
        }
    }

    // ============================================================
    // Verify prescription hash against blockchain
    // ============================================================
    public boolean verifyPrescription(String prescriptionId, String recordHash) {
        try {
            byte[] hashBytes = hexStringToBytes32(recordHash);

            // Wrapper returns a Boolean directly
            Boolean isValid = contract.verifyPrescriptionHash(
                    prescriptionId, hashBytes).send();

            log.info("🔍 Verification result for {}: {}", prescriptionId, isValid);
            return isValid != null && isValid;

        } catch (Exception e) {
            log.error("❌ Verification failed: {}", e.getMessage());
            return false;
        }
    }

    // ============================================================
    // Check if prescription exists on blockchain
    // ============================================================
    public boolean existsOnChain(String prescriptionId) {
        try {
            return contract.prescriptionExistsOnChain(prescriptionId).send();
        } catch (Exception e) {
            log.error("❌ Existence check failed: {}", e.getMessage());
            return false;
        }
    }

    // ============================================================
    // Get total records stored
    // ============================================================
    public Long getTotalRecords() {
        try {
            BigInteger total = contract.getTotalRecords().send();
            return total.longValue();
        } catch (Exception e) {
            return 0L;
        }
    }

    // ============================================================
    // Helper — convert hex string to bytes32 array
    // ============================================================
    private byte[] hexStringToBytes32(String hex) {
        String cleanHex = hex.startsWith("0x") ? hex.substring(2) : hex;
        byte[] bytes    = new byte[32];
        byte[] decoded  = hexToBytes(cleanHex);
        System.arraycopy(decoded, 0, bytes,
                Math.max(0, 32 - decoded.length),
                Math.min(decoded.length, 32));
        return bytes;
    }

    private byte[] hexToBytes(String hex) {
        int len    = hex.length();
        byte[] out = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            out[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
        }
        return out;
    }

    // ============================================================
    // Check if blockchain service is healthy
    // ============================================================
    public boolean isHealthy() {
        try {
            web3j.ethBlockNumber().send().getBlockNumber();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
