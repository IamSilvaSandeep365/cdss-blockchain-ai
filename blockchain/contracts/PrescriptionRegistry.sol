// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

/**
 * CDSS Prescription Registry
 * Stores SHA-256 hashes of prescriptions on Ethereum blockchain
 * for tamper-proof audit trail
 */
contract PrescriptionRegistry {

    // ============================================================
    // Data Structures
    // ============================================================
    struct PrescriptionRecord {
        string  prescriptionId;   // PostgreSQL prescription ID
        bytes32 recordHash;       // SHA-256 hash of prescription data
        address storedBy;         // Ethereum address that stored it
        uint256 timestamp;        // Block timestamp
        bool    exists;           // Check if record exists
    }

    // ============================================================
    // State Variables
    // ============================================================
    address public owner;
    uint256 public totalRecords;

    // prescriptionId → PrescriptionRecord
    mapping(string => PrescriptionRecord) private records;

    // List of all prescription IDs (for iteration)
    string[] private prescriptionIds;

    // ============================================================
    // Events (emitted when something happens — logged on blockchain)
    // ============================================================
    event PrescriptionStored(
        string  indexed prescriptionId,
        bytes32         recordHash,
        address         storedBy,
        uint256         timestamp
    );

    event PrescriptionVerified(
        string  indexed prescriptionId,
        bool            isValid,
        uint256         timestamp
    );

    // ============================================================
    // Constructor
    // ============================================================
    constructor() {
        owner        = msg.sender;
        totalRecords = 0;
    }

    // ============================================================
    // Modifiers
    // ============================================================
    modifier onlyOwner() {
        require(msg.sender == owner,
                "Only contract owner can call this function");
        _;
    }

    modifier prescriptionExists(string memory prescriptionId) {
        require(records[prescriptionId].exists,
                "Prescription record not found on blockchain");
        _;
    }

    // ============================================================
    // FUNCTION 1 — Store a prescription hash
    // Called by Spring Boot when a prescription is finalized
    // ============================================================
    function storePrescriptionHash(
        string  memory prescriptionId,
        bytes32        recordHash
    ) public returns (bool) {

        // Prevent overwriting existing records
        require(!records[prescriptionId].exists,
                "Prescription already stored on blockchain");

        // Store the record
        records[prescriptionId] = PrescriptionRecord({
            prescriptionId: prescriptionId,
            recordHash:     recordHash,
            storedBy:       msg.sender,
            timestamp:      block.timestamp,
            exists:         true
        });

        prescriptionIds.push(prescriptionId);
        totalRecords++;

        // Emit event for audit trail
        emit PrescriptionStored(
            prescriptionId,
            recordHash,
            msg.sender,
            block.timestamp
        );

        return true;
    }

    // ============================================================
    // FUNCTION 2 — Verify a prescription hash
    // Called by Spring Boot to verify prescription integrity
    // ============================================================
    function verifyPrescriptionHash(
        string  memory prescriptionId,
        bytes32        hashToVerify
    ) public view prescriptionExists(prescriptionId) returns (bool) {

        bool isValid = records[prescriptionId].recordHash == hashToVerify;

        return isValid;
    }

    // ============================================================
    // FUNCTION 3 — Get prescription record details
    // ============================================================
    function getPrescriptionRecord(
        string memory prescriptionId
    ) public view prescriptionExists(prescriptionId)
      returns (
        bytes32 recordHash,
        address storedBy,
        uint256 timestamp
      )
    {
        PrescriptionRecord memory record = records[prescriptionId];
        return (record.recordHash, record.storedBy, record.timestamp);
    }

    // ============================================================
    // FUNCTION 4 — Check if prescription exists on blockchain
    // ============================================================
    function prescriptionExistsOnChain(
        string memory prescriptionId
    ) public view returns (bool) {
        return records[prescriptionId].exists;
    }

    // ============================================================
    // FUNCTION 5 — Get total number of stored prescriptions
    // ============================================================
    function getTotalRecords() public view returns (uint256) {
        return totalRecords;
    }
}