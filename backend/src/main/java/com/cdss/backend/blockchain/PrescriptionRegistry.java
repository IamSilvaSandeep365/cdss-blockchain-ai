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
    public static final String BINARY = "0x608060405234801561001057600080fd5b50336000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506000600181905550610f12806100686000396000f3fe608060405234801561001057600080fd5b506004361061007d5760003560e01c80638da5cb5b1161005b5780638da5cb5b146100ee578063ad84abb11461010c578063f07bd0171461013c578063f1ea48151461016e5761007d565b80630aeacb5e14610082578063125f8974146100a0578063728fbceb146100be575b600080fd5b61008a61019e565b60405161009791906106a2565b60405180910390f35b6100a86101a8565b6040516100b591906106a2565b60405180910390f35b6100d860048036038101906100d3919061084d565b6101ae565b6040516100e591906108c4565b60405180910390f35b6100f661024c565b6040516101039190610920565b60405180910390f35b6101266004803603810190610121919061093b565b610270565b60405161013391906108c4565b60405180910390f35b6101566004803603810190610151919061093b565b6102a8565b60405161016593929190610993565b60405180910390f35b6101886004803603810190610183919061084d565b61047e565b60405161019591906108c4565b60405180910390f35b6000600154905090565b60015481565b6000826002816040516101c19190610a3b565b908152602001604051809103902060040160009054906101000a900460ff1661021f576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161021690610ad5565b60405180910390fd5b826002856040516102309190610a3b565b9081526020016040518091039020600101541491505092915050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60006002826040516102829190610a3b565b908152602001604051809103902060040160009054906101000a900460ff169050919050565b6000806000836002816040516102be9190610a3b565b908152602001604051809103902060040160009054906101000a900460ff1661031c576040517f08c379a000000000000000000000000000000000000000000000000000000000815260040161031390610ad5565b60405180910390fd5b600060028660405161032e9190610a3b565b90815260200160405180910390206040518060a001604052908160008201805461035790610b24565b80601f016020809104026020016040519081016040528092919081815260200182805461038390610b24565b80156103d05780601f106103a5576101008083540402835291602001916103d0565b820191906000526020600020905b8154815290600101906020018083116103b357829003601f168201915b50505050508152602001600182015481526020016002820160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff168152602001600382015481526020016004820160009054906101000a900460ff161515151581525050905080602001518160400151826060015194509450945050509193909250565b60006002836040516104909190610a3b565b908152602001604051809103902060040160009054906101000a900460ff16156104ef576040517f08c379a00000000000000000000000000000000000000000000000000000000081526004016104e690610bc7565b60405180910390fd5b6040518060a001604052808481526020018381526020013373ffffffffffffffffffffffffffffffffffffffff1681526020014281526020016001151581525060028460405161053f9190610a3b565b908152602001604051809103902060008201518160000190816105629190610d93565b506020820151816001015560408201518160020160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055506060820151816003015560808201518160040160006101000a81548160ff0219169083151502179055509050506003839080600181540180825580915050600190039060005260206000200160009091909190915090816106159190610d93565b506001600081548092919061062990610e94565b91905055508260405161063c9190610a3b565b60405180910390207f6be264ec111216475ec45dffd2999c43b11892926d18ca98c13909df8c9b1db983334260405161067793929190610993565b60405180910390a26001905092915050565b6000819050919050565b61069c81610689565b82525050565b60006020820190506106b76000830184610693565b92915050565b6000604051905090565b600080fd5b600080fd5b600080fd5b600080fd5b6000601f19601f8301169050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052604160045260246000fd5b610724826106db565b810181811067ffffffffffffffff82111715610743576107426106ec565b5b80604052505050565b60006107566106bd565b9050610762828261071b565b919050565b600067ffffffffffffffff821115610782576107816106ec565b5b61078b826106db565b9050602081019050919050565b82818337600083830152505050565b60006107ba6107b584610767565b61074c565b9050828152602081018484840111156107d6576107d56106d6565b5b6107e1848285610798565b509392505050565b600082601f8301126107fe576107fd6106d1565b5b813561080e8482602086016107a7565b91505092915050565b6000819050919050565b61082a81610817565b811461083557600080fd5b50565b60008135905061084781610821565b92915050565b60008060408385031215610864576108636106c7565b5b600083013567ffffffffffffffff811115610882576108816106cc565b5b61088e858286016107e9565b925050602061089f85828601610838565b9150509250929050565b60008115159050919050565b6108be816108a9565b82525050565b60006020820190506108d960008301846108b5565b92915050565b600073ffffffffffffffffffffffffffffffffffffffff82169050919050565b600061090a826108df565b9050919050565b61091a816108ff565b82525050565b60006020820190506109356000830184610911565b92915050565b600060208284031215610951576109506106c7565b5b600082013567ffffffffffffffff81111561096f5761096e6106cc565b5b61097b848285016107e9565b91505092915050565b61098d81610817565b82525050565b60006060820190506109a86000830186610984565b6109b56020830185610911565b6109c26040830184610693565b949350505050565b600081519050919050565b600081905092915050565b60005b838110156109fe5780820151818401526020810190506109e3565b60008484015250505050565b6000610a15826109ca565b610a1f81856109d5565b9350610a2f8185602086016109e0565b80840191505092915050565b6000610a478284610a0a565b915081905092915050565b600082825260208201905092915050565b7f507265736372697074696f6e207265636f7264206e6f7420666f756e64206f6e60008201527f20626c6f636b636861696e000000000000000000000000000000000000000000602082015250565b6000610abf602b83610a52565b9150610aca82610a63565b604082019050919050565b60006020820190508181036000830152610aee81610ab2565b9050919050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052602260045260246000fd5b60006002820490506001821680610b3c57607f821691505b602082108103610b4f57610b4e610af5565b5b50919050565b7f507265736372697074696f6e20616c72656164792073746f726564206f6e206260008201527f6c6f636b636861696e0000000000000000000000000000000000000000000000602082015250565b6000610bb1602983610a52565b9150610bbc82610b55565b604082019050919050565b60006020820190508181036000830152610be081610ba4565b9050919050565b60008190508160005260206000209050919050565b60006020601f8301049050919050565b600082821b905092915050565b600060088302610c497fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff82610c0c565b610c538683610c0c565b95508019841693508086168417925050509392505050565b6000819050919050565b6000610c90610c8b610c8684610689565b610c6b565b610689565b9050919050565b6000819050919050565b610caa83610c75565b610cbe610cb682610c97565b848454610c19565b825550505050565b600090565b610cd3610cc6565b610cde818484610ca1565b505050565b5b81811015610d0257610cf7600082610ccb565b600181019050610ce4565b5050565b601f821115610d4757610d1881610be7565b610d2184610bfc565b81016020851015610d30578190505b610d44610d3c85610bfc565b830182610ce3565b50505b505050565b600082821c905092915050565b6000610d6a60001984600802610d4c565b1980831691505092915050565b6000610d838383610d59565b9150826002028217905092915050565b610d9c826109ca565b67ffffffffffffffff811115610db557610db46106ec565b5b610dbf8254610b24565b610dca828285610d06565b600060209050601f831160018114610dfd5760008415610deb578287015190505b610df58582610d77565b865550610e5d565b601f198416610e0b86610be7565b60005b82811015610e3357848901518255600182019150602085019450602081019050610e0e565b86831015610e505784890151610e4c601f891682610d59565b8355505b6001600288020188555050505b505050505050565b7f4e487b7100000000000000000000000000000000000000000000000000000000600052601160045260246000fd5b6000610e9f82610689565b91507fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff8203610ed157610ed0610e65565b5b60018201905091905056fea2646970667358221220e4a5bf6e5b00d691afd9fe76219e7fc2021ff399f972e02c9ed93bf240a3033364736f6c63430008130033";

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
        _addresses.put("5777", "0xb6a3f4758883a8b3FAcdD7741c7D364796B467f7");
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

    public RemoteFunctionCall<Boolean> verifyPrescriptionHash(String prescriptionId, byte[] hashToVerify) {
        final Function function = new Function(FUNC_VERIFYPRESCRIPTIONHASH, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(prescriptionId), 
                new org.web3j.abi.datatypes.generated.Bytes32(hashToVerify)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
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
