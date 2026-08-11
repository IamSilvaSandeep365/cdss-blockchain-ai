package com.cdss.backend.blockchain;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.10.3.
 */
@SuppressWarnings("rawtypes")
public class PrescriptionRegistry extends Contract {
    public static final String BINARY = "0x608060405234801561001057600080fd5b50336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600181905550610f90806100686000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80638da5cb5b1161005b5780638da5cb5b146100ee578063ad84abb11461010c578063f07bd0171461013c578063f1ea48151461016e5761007d565b80630aeacb5e14610082578063125f8974146100a0578063728fbceb146100be575b600080fd5b61008a61019e565b60405161009791906106f7565b60405180910390f35b6100a86101a8565b6040516100b591906106f7565b60405180910390f35b6100d860048036038101906100d391906108a2565b6101ae565b6040516100e59190610919565b60405180910390f35b6100f66102a1565b6040516101039190610975565b60405180910390f35b61012660048036038101906101219190610990565b6102c5565b6040516101339190610919565b60405180910390f35b61015660048036038101906101519190610990565b6102fd565b604051610165939291906109e8565b60405180910390f35b610188600480360381019061018391906108a2565b6104d3565b6040516101959190610919565b60405180910390f35b6000600154905090565b60015481565b6000826002816040516101c19190610a90565b908152602001604051809103902060040160009054906101000a900460ff1661021f576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161021690610b2a565b60405180910390fd5b6000836002866040516102329190610a90565b908152602001604051809103902060010154149050846040516102559190610a90565b60405180910390207f605ebc66b3c09e48b9452b6321dd30d6768c3f13f482c5a8d53e9f00646018fb824260405161028e929190610b4a565b60405180910390a2809250505092915050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60006002826040516102d79190610a90565b908152602001604051809103902060040160009054906101000a900460ff169050919050565b6000806000836002816040516103139190610a90565b908152602001604051809103902060040160009054906101000a900460ff16610371576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161036890610b2a565b60405180910390fd5b60006002866040516103839190610a90565b90815260200160405180910390206040518060a00160405290816000820180546103ac90610ba2565b80601f01602080910402602001604051908101604052809291908181526020018280546103d890610ba2565b80156104255780601f106103fa57610100808354040283529160200191610425565b820191906000526020600020905b81548152906001019060200180831161040857829003601f168201915b50505050508152602001600182015481526020016002820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600382015481526020016004820160009054906101000a900460ff161515151581525050905080602001518160400151826060015194509450945050509193909250565b60006002836040516104e59190610a90565b908152602001604051809103902060040160009054906101000a900460ff1615610544576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161053b90610c45565b60405180910390fd5b6040518060a001604052808481526020018381526020013373ffffffffffffffffffffffffffffffffffffffff168152602001428152602001600115158152506002846040516105949190610a90565b908152602001604051809103902060008201518160000190816105b79190610e11565b506020820151816001015560408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506060820151816003015560808201518160040160006101000a81548160ff02191690831515021790555090505060038390806001815401808255809150506001900390600052602060002001600090919091909150908161066a9190610e11565b506001600081548092919061067e90610f12565b9190505550826040516106919190610a90565b60405180910390207f6be264ec111216475ec45dffd2999c43b11892926d18ca98c13909df8c9b1db98333426040516106cc939291906109e8565b60405180910390a26001905092915050565b6000819050919050565b6106f1816106de565b82525050565b600060208201905061070c60008301846106e8565b92915050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b61077982610730565b810181811067ffffffffffffffff8211171561079857610797610741565b5b80604052505050565b60006107ab610712565b90506107b78282610770565b919050565b600067ffffffffffffffff8211156107d7576107d6610741565b5b6107e082610730565b9050602081019050919050565b82818337600083830152505050565b600061080f61080a846107bc565b6107a1565b90508281526020810184848401111561082b5761082a61072b565b5b6108368482856107ed565b509392505050565b600082601f83011261085357610852610726565b5b81356108638482602086016107fc565b91505092915050565b6000819050919050565b61087f8161086c565b811461088a57600080fd5b50565b60008135905061089c81610876565b92915050565b600080604083850312156108b9576108b861071c565b5b600083013567ffffffffffffffff8111156108d7576108d6610721565b5b6108e38582860161083e565b92505060206108f48582860161088d565b9150509250929050565b60008115159050919050565b610913816108fe565b82525050565b600060208201905061092e600083018461090a565b92915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b600061095f82610934565b9050919050565b61096f81610954565b82525050565b600060208201905061098a6000830184610966565b92915050565b6000602082840312156109a6576109a561071c565b5b600082013567ffffffffffffffff8111156109c4576109c3610721565b5b6109d08482850161083e565b91505092915050565b6109e28161086c565b82525050565b60006060820190506109fd60008301866109d9565b610a0a6020830185610966565b610a1760408301846106e8565b949350505050565b600081519050919050565b600081905092915050565b60005b83811015610a53578082015181840152602081019050610a38565b60008484015250505050565b6000610a6a82610a1f565b610a748185610a2a565b9350610a84818560208601610a35565b80840191505092915050565b6000610a9c8284610a5f565b915081905092915050565b600082825260208201905092915050565b7f507265736372697074696f6e207265636f7264206e6f7420666f756e64206f6e60008201527f20626c6f636b636861696e000000000000000000000000000000000000000000602082015250565b6000610b14602b83610aa7565b9150610b1f82610ab8565b604082019050919050565b60006020820190508181036000830152610b4381610b07565b9050919050565b6000604082019050610b5f600083018561090a565b610b6c60208301846106e8565b9392505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b60006002820490506001821680610bba57607f821691505b602082108103610bcd57610bcc610b73565b5b50919050565b7f507265736372697074696f6e20616c72656164792073746f726564206f6e206260008201527f6c6f636b636861696e0000000000000000000000000000000000000000000000602082015250565b6000610c2f602983610aa7565b9150610c3a82610bd3565b604082019050919050565b60006020820190508181036000830152610c5e81610c22565b9050919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b600060088302610cc77fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610c8a565b610cd18683610c8a565b95508019841693508086168417925050509392505050565b6000819050919050565b6000610d0e610d09610d04846106de565b610ce9565b6106de565b9050919050565b6000819050919050565b610d2883610cf3565b610d3c610d3482610d15565b848454610c97565b825550505050565b600090565b610d51610d44565b610d5c818484610d1f565b505050565b5b81811015610d8057610d75600082610d49565b600181019050610d62565b5050565b601f821115610dc557610d9681610c65565b610d9f84610c7a565b81016020851015610dae578190505b610dc2610dba85610c7a565b830182610d61565b50505b505050565b600082821c905092915050565b6000610de860001984600802610dca565b1980831691505092915050565b6000610e018383610dd7565b9150826002028217905092915050565b610e1a82610a1f565b67ffffffffffffffff811115610e3357610e32610741565b5b610e3d8254610ba2565b610e48828285610d84565b600060209050601f831160018114610e7b5760008415610e69578287015190505b610e738582610df5565b865550610edb565b601f198416610e8986610c65565b60005b82811015610eb157848901518255600182019150602085019450602081019050610e8c565b86831015610ece5784890151610eca601f891682610dd7565b8355505b6001600288020188555050505b505050505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000610f1d826106de565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8203610f4f57610f4e610ee3565b5b60018201905091905056fea2646970667358221220c021b8859967dd0a171296ef60e3041b6f63714893487979cc4ee3560f2cbd8564736f6c63430008130033";

    public static final String FUNC_OWNER = "owner";

    public static final String FUNC_TOTALRECORDS = "totalRecords";

    public static final String FUNC_STOREPRESCRIPTIONHASH = "storePrescriptionHash";

    public static final String FUNC_VERIFYPRESCRIPTIONHASH = "verifyPrescriptionHash";

    public static final String FUNC_GETPRESCRIPTIONRECORD = "getPrescriptionRecord";

    public static final String FUNC_PRESCRIPTIONEXISTSONCHAIN = "prescriptionExistsOnChain";

    public static final String FUNC_GETTOTALRECORDS = "getTotalRecords";

    public static final Event PRESCRIPTIONSTORED_EVENT = new Event("PrescriptionStored", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}, new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event PRESCRIPTIONVERIFIED_EVENT = new Event("PrescriptionVerified", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>(true) {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}));
    ;

    protected static final HashMap<String, String> _addresses;

    static {
        _addresses = new HashMap<String, String>();
        _addresses.put("5777", "0x22455637725C85526E55f6b54D05f5cf2936F431");
    }

    @Deprecated
    protected PrescriptionRegistry(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PrescriptionRegistry(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected PrescriptionRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected PrescriptionRegistry(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<PrescriptionStoredEventResponse> getPrescriptionStoredEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PRESCRIPTIONSTORED_EVENT, transactionReceipt);
        ArrayList<PrescriptionStoredEventResponse> responses = new ArrayList<PrescriptionStoredEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PrescriptionStoredEventResponse typedResponse = new PrescriptionStoredEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.prescriptionId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.recordHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.storedBy = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PrescriptionStoredEventResponse getPrescriptionStoredEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PRESCRIPTIONSTORED_EVENT, log);
        PrescriptionStoredEventResponse typedResponse = new PrescriptionStoredEventResponse();
        typedResponse.log = log;
        typedResponse.prescriptionId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.recordHash = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.storedBy = (String) eventValues.getNonIndexedValues().get(1).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(2).getValue();
        return typedResponse;
    }

    public Flowable<PrescriptionStoredEventResponse> prescriptionStoredEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPrescriptionStoredEventFromLog(log));
    }

    public Flowable<PrescriptionStoredEventResponse> prescriptionStoredEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PRESCRIPTIONSTORED_EVENT));
        return prescriptionStoredEventFlowable(filter);
    }

    public static List<PrescriptionVerifiedEventResponse> getPrescriptionVerifiedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(PRESCRIPTIONVERIFIED_EVENT, transactionReceipt);
        ArrayList<PrescriptionVerifiedEventResponse> responses = new ArrayList<PrescriptionVerifiedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            PrescriptionVerifiedEventResponse typedResponse = new PrescriptionVerifiedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.prescriptionId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.isValid = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static PrescriptionVerifiedEventResponse getPrescriptionVerifiedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(PRESCRIPTIONVERIFIED_EVENT, log);
        PrescriptionVerifiedEventResponse typedResponse = new PrescriptionVerifiedEventResponse();
        typedResponse.log = log;
        typedResponse.prescriptionId = (byte[]) eventValues.getIndexedValues().get(0).getValue();
        typedResponse.isValid = (Boolean) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.timestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<PrescriptionVerifiedEventResponse> prescriptionVerifiedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getPrescriptionVerifiedEventFromLog(log));
    }

    public Flowable<PrescriptionVerifiedEventResponse> prescriptionVerifiedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PRESCRIPTIONVERIFIED_EVENT));
        return prescriptionVerifiedEventFlowable(filter);
    }

    public RemoteFunctionCall<String> owner() {
        final Function function = new Function(FUNC_OWNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> totalRecords() {
        final Function function = new Function(FUNC_TOTALRECORDS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> storePrescriptionHash(String prescriptionId, byte[] recordHash) {
        final Function function = new Function(
                FUNC_STOREPRESCRIPTIONHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(prescriptionId), 
                new org.web3j.abi.datatypes.generated.Bytes32(recordHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> verifyPrescriptionHash(String prescriptionId, byte[] hashToVerify) {
        final Function function = new Function(
                FUNC_VERIFYPRESCRIPTIONHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(prescriptionId), 
                new org.web3j.abi.datatypes.generated.Bytes32(hashToVerify)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple3<byte[], String, BigInteger>> getPrescriptionRecord(String prescriptionId) {
        final Function function = new Function(FUNC_GETPRESCRIPTIONRECORD, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(prescriptionId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Address>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple3<byte[], String, BigInteger>>(function,
                new Callable<Tuple3<byte[], String, BigInteger>>() {
                    @Override
                    public Tuple3<byte[], String, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<byte[], String, BigInteger>(
                                (byte[]) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<Boolean> prescriptionExistsOnChain(String prescriptionId) {
        final Function function = new Function(FUNC_PRESCRIPTIONEXISTSONCHAIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(prescriptionId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> getTotalRecords() {
        final Function function = new Function(FUNC_GETTOTALRECORDS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    @Deprecated
    public static PrescriptionRegistry load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrescriptionRegistry(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static PrescriptionRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PrescriptionRegistry(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static PrescriptionRegistry load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new PrescriptionRegistry(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static PrescriptionRegistry load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new PrescriptionRegistry(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<PrescriptionRegistry> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrescriptionRegistry.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    public static RemoteCall<PrescriptionRegistry> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(PrescriptionRegistry.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrescriptionRegistry> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrescriptionRegistry.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<PrescriptionRegistry> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PrescriptionRegistry.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    protected String getStaticDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static String getPreviouslyDeployedAddress(String networkId) {
        return _addresses.get(networkId);
    }

    public static class PrescriptionStoredEventResponse extends BaseEventResponse {
        public byte[] prescriptionId;

        public byte[] recordHash;

        public String storedBy;

        public BigInteger timestamp;
    }

    public static class PrescriptionVerifiedEventResponse extends BaseEventResponse {
        public byte[] prescriptionId;

        public Boolean isValid;

        public BigInteger timestamp;
    }
}
