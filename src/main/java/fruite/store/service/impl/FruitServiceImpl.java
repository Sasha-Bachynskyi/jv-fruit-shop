package fruite.store.service.impl;

import fruite.store.db.Storage;
import fruite.store.service.FruitService;
import fruite.store.service.FileReaderService;
import fruite.store.service.FileWriterService;
import fruite.store.service.strategy.BalanceOperationType;
import fruite.store.service.strategy.PurchaseOperationType;
import fruite.store.service.strategy.ReturnOperationType;
import fruite.store.service.strategy.StrategyType;
import fruite.store.service.strategy.SupplyOperationType;
import java.util.Map;

public class FruitServiceImpl implements FruitService {
    private static final int INDEX_OF_OPERATION_TYPE = 0;
    private static final int INDEX_OF_FRUIT_TYPE = 1;
    private static final int INDEX_OF_FRUIT_QUANTITY = 2;
    private static final String FRUIT_WORD = "fruit";
    private static final String QUANTITY_WORD = "quantity";
    private static final String COMA_SEPARATOR = ",";
    private StrategyType strategyType;
    private FileReaderService readDateDao;
    private FileWriterService writeDateDao;

    public FruitServiceImpl(StrategyType strategyType,
                            FileReaderService readDateDao,
                            FileWriterService writeDateDao) {
        this.strategyType = strategyType;
        this.readDateDao = readDateDao;
        this.writeDateDao = writeDateDao;
    }

    @Override
    public void makeReportByDay(String fromFilePath, String toFilePath) {
        String data = readDateDao.readFromFile(fromFilePath);
        strategyInitilization();
        processDate(data);
        byte[] report = generateDataForReport();
        writeDateDao.writeToFile(report, toFilePath);
    }

    private void strategyInitilization() {
        StrategyType.operationTypeStrategy.put("b", new BalanceOperationType());
        StrategyType.operationTypeStrategy.put("s", new SupplyOperationType());
        StrategyType.operationTypeStrategy.put("p", new PurchaseOperationType());
        StrategyType.operationTypeStrategy.put("r", new ReturnOperationType());
    }

    private void processDate(String data) {
        String[] arrayData = data.split(System.lineSeparator());
        for (int i = 1; i < arrayData.length; i++) {
            String[] temp = arrayData[i].split(COMA_SEPARATOR);
            strategyType.doSpecialOperationOnFruits(
                    temp[INDEX_OF_OPERATION_TYPE],
                    temp[INDEX_OF_FRUIT_TYPE],
                    temp[INDEX_OF_FRUIT_QUANTITY]);
        }
    }

    private byte[] generateDataForReport() {
        StringBuilder builderResult = new StringBuilder();
        builderResult.append(FRUIT_WORD).append(COMA_SEPARATOR).append(QUANTITY_WORD);
        for (Map.Entry<String, Integer> entry : Storage.fruitStorage.entrySet()) {
            builderResult.append(System.lineSeparator())
                    .append(entry.getKey())
                    .append(COMA_SEPARATOR)
                    .append(entry.getValue());
        }
        return builderResult.toString().getBytes();
    }
}