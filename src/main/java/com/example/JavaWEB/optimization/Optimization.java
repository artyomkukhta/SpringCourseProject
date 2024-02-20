package com.example.JavaWEB.optimization;

import com.example.JavaWEB.model.Card;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Optimization {
    public int minmaxResult = 0;
    public int binaryResult = 0;
    public int mainCriteriaResult = 0;
    public Card calculate(List<Card> originalCards) {
        Double[][] matrix = new Double[originalCards.size()][3];
        for (int i = 0; i<originalCards.size(); i++) {
            matrix[i][0]=originalCards.get(i).getCardType().getCashback();
            matrix[i][1]=originalCards.get(i).getCardType().getCommission();
            matrix[i][2]=originalCards.get(i).getCardType().getLimit()-originalCards.get(i).getOperationSum();
        }
        basePrintMatrix(matrix);
        List<Condition> conditions = Arrays.asList(
                new Condition(1, true, 0.1),
                new Condition(2, false, 0.2),
                new Condition(3, true, 0.1)
        );
        boolean onMax = true;
        Result result = binaryOptimization(matrix, conditions);
        result.setMinPointsNums(result.getMinPointsNums().stream()
                .distinct()
                .collect(Collectors.toList()));
        Double[][] newMatrix = removeRows(matrix, result.getMinPointsNums());
        Double[][] normalizeMatrix = normalizeMatrix(newMatrix);
        Double[][] adjustedMatrix = adjustMatrix(normalizeMatrix, onMax, conditions);
        List<Double> minMax = getMinMaxValues(adjustedMatrix, onMax);
        Pair<Map<Integer, Double>, Double> mainCriteriaPair = mainCriteria(adjustedMatrix, conditions);
        System.out.println("Binary Matrix:");
        Map<Integer, Double> sums = printMatrix(result.getMatrix(), result.getFailMatrix(), result.getMaxPointsNums(), result.getMinPointsNums());
        binaryResult = findMaxKey(sums);
        System.out.println("Answer: " + binaryResult);
        System.out.println("New Matrix:");
        basePrintMatrix(newMatrix);
        System.out.println("Normalized Matrix:");
        basePrintMatrix(normalizeMatrix);
        System.out.println("Adjusted Matrix:");
        basePrintMatrix(adjustedMatrix);
        System.out.println("MinMax:");
        System.out.println(String.join("| ", minMax.toString()));
        if (onMax) {
            System.out.println("Answer " + minMax.stream().mapToDouble(Double::doubleValue).max().orElse(0));
            minmaxResult = recalculateIndex(matrix.length, IntStream.range(0, minMax.size())
                    .boxed()
                    .max(Comparator.comparing(minMax::get))
                    .orElse(-1), result.getMinPointsNums());
            System.out.println("index " + minmaxResult);
        } else {
            System.out.println("Answer " + minMax.stream().mapToDouble(Double::doubleValue).min().orElse(0));
            minmaxResult = recalculateIndex(matrix.length,IntStream.range(0, minMax.size())
                    .boxed()
                    .min(Comparator.comparing(minMax::get))
                    .orElse(-1), result.getMinPointsNums());
            System.out.println("index " + minmaxResult);
        }
        System.out.println("Main criteria:");
        System.out.println("Main criteria upper limit:");
        System.out.println(mainCriteriaPair.second);
        Optional<Map.Entry<Integer, Double>> answer;

        for (Map.Entry<Integer, Double> entry : mainCriteriaPair.first.entrySet()) {
            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
        }
        if (!onMax)
            answer = mainCriteriaPair.first.entrySet().stream().min(Map.Entry.comparingByValue());
        else
            answer = mainCriteriaPair.first.entrySet().stream().max(Map.Entry.comparingByValue());

        mainCriteriaResult = recalculateIndex(matrix.length,answer.get().getKey(), result.getMinPointsNums());
        System.out.println("Main criteria answer: \nX:" + mainCriteriaResult + " | Value:" + answer.get().getValue());
        System.out.println(minmaxResult);
        System.out.println(binaryResult);
        System.out.println(mainCriteriaResult);
        List<Integer> answers =  new ArrayList<>();
        answers.add(minmaxResult);
        answers.add(binaryResult);
        answers.add(mainCriteriaResult);
        int finalIndex = findMostFrequentNumber(answers)-1;

        return originalCards.get(finalIndex);
    }

    public Integer findMostFrequentNumber(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            // Обработка случая пустого списка или null
            return null;
        }

        // Используем Map для подсчета частоты каждого числа
        Map<Integer, Integer> frequencyMap = new HashMap<>();
        for (Integer number : numbers) {
            frequencyMap.put(number, frequencyMap.getOrDefault(number, 0) + 1);
        }

        // Находим число с максимальной частотой
        Integer mostFrequentNumber = null;
        int maxFrequency = 0;
        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if (entry.getValue() > maxFrequency) {
                maxFrequency = entry.getValue();
                mostFrequentNumber = entry.getKey();
            }
        }

        return mostFrequentNumber;
    }

    public int findMinIndex(List<Double> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List must not be null or empty");
        }

        int minIndex = 0;
        double minValue = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) < minValue) {
                minValue = list.get(i);
                minIndex = i;
            }
        }

        return minIndex;
    }
    public int findMaxIndex(List<Double> list) {
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List must not be null or empty");
        }

        int maxIndex = 0;
        double maxValue = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            if (list.get(i) > maxValue) {
                maxValue = list.get(i);
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    private int recalculateIndex(int matrixSize,int prevIndex, List<Integer> consistencyPoints) {
        int finalIndex = prevIndex;
        int add = 0;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < consistencyPoints.size(); j++) {
                if (i == consistencyPoints.get(j)) add++;
            }
        }
        return finalIndex + add + 1;
    }

    private Integer findMaxKey(Map<Integer, Double> map) {
        if (map.isEmpty()) {
            // Handle the case where the map is empty
            return null;
        }

        // Initialize variables to track the max value and its corresponding key
        double maxValue = Double.MIN_VALUE;
        Integer maxKey = null;

        // Iterate through the entries of the map
        for (Map.Entry<Integer, Double> entry : map.entrySet()) {
            double value = entry.getValue();
            if (value > maxValue) {
                // Update the max value and its corresponding key
                maxValue = value;
                maxKey = entry.getKey();
            }
        }

        // Return the key with the maximum value
        return maxKey + 1;
    }



    Result binaryOptimization(Double[][] matrix, List<Condition> conditions) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        Double[][] newMatrix = new Double[rows][rows];
        Double[][] newFailMatrix = new Double[rows][rows];
        List<Integer> maxPointsNums = new ArrayList<>();
        List<Integer> minPointsNums = new ArrayList<>();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < rows; j++) {
                if (i == j) {
                    newMatrix[i][j] = (double) cols / 2; // Assuming you want to convert to Double
                    newMatrix[i][j] = null;
                    newFailMatrix[i][j] = null;
                } else {
                    double passed = 0;
                    double failed = 0;

                    for (int k = 0; k < cols; k++) {
                        Double value1 = matrix[i][k];
                        Double value2 = matrix[j][k];
                        boolean condition = conditions.get(k).isValue();

                        if (value1.doubleValue() == value2.doubleValue()) {
                            passed += 0.5;
                            failed += 0.5;
                        } else if ((condition && value1.doubleValue() > value2.doubleValue()) || (!condition && value1.doubleValue() < value2.doubleValue())) {
                            passed++;
                        } else {
                            failed++;
                        }
                    }

                    newMatrix[i][j] = passed;
                    newFailMatrix[i][j] = failed;
                    if (failed == 0 || failed == 0.5)
                        maxPointsNums.add(i);
                    if (passed == 0 || passed == 0.5)
                        minPointsNums.add(i);
                }
            }
        }

        Result result = new Result();
        result.setMatrix(newMatrix);
        result.setFailMatrix(newFailMatrix);
        result.setMaxPointsNums(maxPointsNums);
        result.setMinPointsNums(minPointsNums);

        return result;
    }


    Double[][] removeRows(Double[][] matrix, List<Integer> rowsToRemove) {
        int numRows = matrix.length;
        int numCols = matrix[0].length;

        List<Integer> rowsToKeep = new ArrayList<>();
        for (int i = 0; i < numRows; i++) {
            if (!rowsToRemove.contains(i + 1)) {
                rowsToKeep.add(i);
            }
        }

        Double[][] newMatrix = new Double[rowsToKeep.size()][numCols];

        for (int i = 0; i < rowsToKeep.size(); i++) {
            int rowIndex = rowsToKeep.get(i);
            System.arraycopy(matrix[rowIndex], 0, newMatrix[i], 0, numCols);
        }

        return newMatrix;
    }

    public Double[][] removeColumn(Double[][] matrix, int columnIndex) {
        int numRows = matrix.length;
        int numCols = matrix[0].length - 1;  // New number of columns

        if (columnIndex < 0 || columnIndex > numCols) {
            throw new IllegalArgumentException("Invalid column index");
        }

        Double[][] newMatrix = new Double[numRows][numCols];

        for (int i = 0; i < numRows; i++) {
            int destCol = 0;
            for (int srcCol = 0; srcCol < matrix[i].length; srcCol++) {
                if (srcCol != columnIndex) {
                    newMatrix[i][destCol++] = matrix[i][srcCol];
                }
            }
        }

        return newMatrix;
    }

    Double[][] normalizeMatrix(Double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Double[][] normalizedMatrix = new Double[rows][cols];

        for (int j = 0; j < cols; j++) {
            double minVal = matrix[0][j];
            double maxVal = matrix[0][j];
            for (int i = 1; i < rows; i++) {
                if (matrix[i][j] < minVal) {
                    minVal = matrix[i][j];
                }
                if (matrix[i][j] > maxVal) {
                    maxVal = matrix[i][j];
                }
            }

            for (int i = 0; i < rows; i++) {
                double normalizedValue = (matrix[i][j] - minVal) / (maxVal - minVal);
                normalizedMatrix[i][j] = Math.round(normalizedValue * 100.0) / 100.0;
            }
        }

        return normalizedMatrix;
    }

    Double[][] adjustMatrix(Double[][] normalizeMatrix, boolean onMax, List<Condition> conditions) {
        int numRows = normalizeMatrix.length;
        int numCols = normalizeMatrix[0].length;
        Double[][] adjustedMatrix = new Double[numRows][numCols];

        for (int col = 0; col < numCols; col++) {
            final int currentCol = col;  // Make a final copy of col
            Condition condition = conditions.stream().filter(c -> c.getColumn() == currentCol + 1).findFirst().orElse(null);
            boolean useOriginalValue = condition != null && condition.isValue() == onMax;

            for (int row = 0; row < numRows; row++) {
                double currentValue = normalizeMatrix[row][currentCol];
                adjustedMatrix[row][currentCol] = useOriginalValue ? currentValue : 1 - currentValue;
            }
        }


        return adjustedMatrix;
    }

    List<Double> getMinMaxValues(Double[][] matrix, boolean onMax) {
        List<Double> minMaxValues = new ArrayList<>();


        for (Double[] row : matrix) {

            double value = onMax ? Arrays.stream(row).min(Double::compareTo).orElse(0.0) :
                    Arrays.stream(row).max(Double::compareTo).orElse(0.0);
            minMaxValues.add(value);
        }

        return minMaxValues;
    }

    Map<Integer, Double> printMatrix(Double[][] matrix, Double[][] failMatrix, List<Integer> maxPointsNums, List<Integer> minPointsNums) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        Map<Integer, Double> sums = new HashMap<>();
        for (int i = 0; i < rows; i++) {
            double sum = 0;
            for (int j = 0; j < cols; j++) {
                sum += matrix[i][j] != null ? matrix[i][j] : 0;
                if (i == j) {
                    System.out.print("   *:*   |");
                } else {
                    System.out.printf("   %.2f:%s   |", matrix[i][j], failMatrix[i][j]);
                }
            }
            System.out.println("S=" + (sum));
            sums.put(i, sum);
            System.out.println("------------------------------------------------------------------------------");

        }
        System.out.println("Consistency points: " + String.join(", ", minPointsNums.stream().distinct().map(Object::toString).collect(Collectors.toList())));
        System.out.println("Maximum points: " + String.join(", ", maxPointsNums.stream().distinct().map(Object::toString).collect(Collectors.toList())));
        return sums;
    }

    void basePrintMatrix(Double[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;

        for (Double[] row : matrix) {
            for (double value : row) {
                System.out.printf("%.2f |", value);
            }
            System.out.println();
            System.out.println("---------------------------");
        }
    }


    public Pair<Map<Integer, Double>, Double> mainCriteria(Double[][] adjustedMatrix, List<Condition> conditions) {
        double maxLambda = conditions.stream().mapToDouble(Condition::getLambda).max().orElse(0.0);
        int columnToRemove = conditions.stream()
                .filter(c -> c.getLambda() == maxLambda)
                .findFirst()
                .map(Condition::getColumn)
                .orElse(0);

        Double[][] newMatrix = removeColumn(adjustedMatrix, columnToRemove - 1);


        List<Double> maxValues = Arrays.stream(newMatrix)
                .flatMap(Arrays::stream)
                .filter(val -> val < 1)
                .collect(Collectors.groupingBy(val -> val, Collectors.maxBy(Comparator.naturalOrder())))
                .values()
                .stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());


        if (maxValues.size()==0){
            maxValues.add(0.0);
        }

        Double maxMaxValue = Collections.max(maxValues);



        Map<Integer, Double> resultDictionary = new HashMap<>();

        for (int i = 0; i < newMatrix.length; i++) {

            if (Arrays.stream(newMatrix[i]).allMatch(val -> val <= maxMaxValue)) {
                resultDictionary.put(i , adjustedMatrix[i][columnToRemove - 1]);
            }
        }

        if (!resultDictionary.isEmpty()) {
            return new Pair<>(resultDictionary, maxMaxValue);
        } else {
            resultDictionary.clear();
            for (int i = 0; i < newMatrix.length; i++) {
                if (Arrays.stream(newMatrix[i]).allMatch(val -> val <= 1)) {
                    resultDictionary.put(i , adjustedMatrix[i][columnToRemove - 1]);
                }
            }
        }

        return new Pair<>(resultDictionary, maxMaxValue);
    }
}

class Pair<T, U> {
    T first;
    U second;

    public Pair(T first, U second) {
        this.first = first;
        this.second = second;
    }
}

class Condition {
    private final int column;
    private final boolean value;
    private final double lambda;
    public Condition(int column, boolean value, double lambda) {
        this.column = column;
        this.value = value;
        this.lambda = lambda;
    }
    public int getColumn() {
        return column;
    }
    public boolean isValue() {
        return value;
    }
    public double getLambda() {
        return lambda;
    }
}

class Result {
    private Double[][] matrix;
    private Double[][] failMatrix;
    private List<Integer> maxPointsNums = new ArrayList<>();
    private List<Integer> minPointsNums = new ArrayList<>();
    public Double[][] getMatrix() {
        return matrix;
    }
    public void setMatrix(Double[][] matrix) {
        this.matrix = matrix;
    }
    public Double[][] getFailMatrix() {
        return failMatrix;
    }
    public void setFailMatrix(Double[][] failMatrix) {
        this.failMatrix = failMatrix;
    }
    public List<Integer> getMaxPointsNums() {
        return maxPointsNums;
    }
    public void setMaxPointsNums(List<Integer> maxPointsNums) {
        this.maxPointsNums = maxPointsNums;
    }
    public List<Integer> getMinPointsNums() {
        return minPointsNums;
    }
    public void setMinPointsNums(List<Integer> minPointsNums) {
        this.minPointsNums = minPointsNums;
    }
}