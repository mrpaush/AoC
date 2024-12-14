package org.example;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.println(day9part2());
    }

    public static long day9part2() throws Exception {
        long answer = 0L;
        String numbers = Files.readString(Paths.get("src/main/resources/day9.txt"));

        System.out.println(numbers);

        StringBuilder correctSize = new StringBuilder();
        char[] charArray = numbers.toCharArray();
        HashMap<Integer, Integer> trackIdIndex = new HashMap<>();
        for (int i = 0; i < charArray.length; i++) {
            char currentChar = charArray[i];
            if (i % 2 == 1) {
                correctSize.append(".".repeat((int) currentChar));
            } else {
                for (int j = 0; j < (int) currentChar; j++) {
                    trackIdIndex.put(correctSize.length() + j, i / 2);
                }
                correctSize.append((String.valueOf((i / 2) % 10)).repeat((int) currentChar));
            }
        }

        System.out.println(correctSize);
        int firstSpaceIndex = correctSize.indexOf(".");

        int finalSize = correctSize.length() - 1;
        for (int i = finalSize; i > firstSpaceIndex; i--) {
            char current = correctSize.charAt(i);
            if (current != '.') {
                int fileLength = 1;
                while (i - fileLength >= 0 && correctSize.charAt(i - fileLength) == current) {
                    fileLength++;
                }
                int fitIndex = correctSize.indexOf(".".repeat(fileLength));
                if (fitIndex != -1 && fitIndex + fileLength-1 < i - fileLength+1) {
                    correctSize.replace(fitIndex, fitIndex + fileLength, String.valueOf(current).repeat(fileLength));
                    for (int j = 0; j < fileLength; j++) {
                        trackIdIndex.put(fitIndex + j, trackIdIndex.get(i));
                        trackIdIndex.remove(i);
                        correctSize.replace(i, i + 1, ".");
                        i--;
                    }
                    i++;
                } else {
                    for (int j = 0; j < fileLength - 1; j++) {
                        i--;
                    }
                }
            }
        }

        for (int i = 0; i < correctSize.length(); i++) {
            if (correctSize.charAt(i) != '.') {
                answer += trackIdIndex.get(i).longValue() * i;
            }
        }
        System.out.println(correctSize);
        return answer;
    }

    public static long day9part1() throws Exception {
        long answer = 0L;
        String numbers = Files.readString(Paths.get("src/main/resources/day9.txt"));
        StringBuilder correctSize = new StringBuilder();
        int originalListLength = numbers.length();
        HashMap<Integer, Integer> trackIdIndex = new HashMap<>();
        int spaceCounter = 0;
        for (int i = 0; i < originalListLength; i++) {
            char currentChar = numbers.charAt(i);
            int times = Integer.parseInt(String.valueOf(currentChar));
            if (i % 2 == 1) {
                correctSize.append(".".repeat(times));
                spaceCounter += times;
            } else {
                for (int j = 0; j < times; j++) {
                    trackIdIndex.put(correctSize.length() + j, i / 2);
                }
                correctSize.append((String.valueOf((i / 2) % 10)).repeat(times));
            }
        }
        int finalSize = correctSize.length() - 1;
        for (int i = finalSize; i > finalSize - spaceCounter; i--) {
            char current = correctSize.charAt(i);
            if (current == '.') {
                correctSize.deleteCharAt(i);
            } else {
                int startAndEnd = correctSize.indexOf(".");
                trackIdIndex.put(startAndEnd, trackIdIndex.get(i));
                trackIdIndex.remove(i);
                correctSize.replace(startAndEnd, startAndEnd + 1, String.valueOf(current));
                correctSize.deleteCharAt(i);
            }
        }

        for (int i = 0; i < correctSize.length(); i++) {
            answer += trackIdIndex.get(i).longValue() * i;
        }
        System.out.println(numbers);
        System.out.println(correctSize);
        return answer;
    }

    public static long day8part2() throws Exception {
        long answer = 0L;
        List<char[]> stringList = Files.readAllLines(Paths.get("src/main/resources/day8.txt")).stream()
                .map(String::toCharArray)
                .toList();
        HashMap<Character, List<Point>> pointMap = new HashMap<>();
        List<Point> pointies = new ArrayList<>();
        int amountRows = stringList.size();
        int amountCols = stringList.getFirst().length;
        for (int i = 0; i < amountRows; i++) {
            for (int j = 0; j < amountCols; j++) {
                char current = stringList.get(i)[j];
                if (current != '.') pointMap.computeIfAbsent(current, k -> new ArrayList<>()).add(new Point(i, j));
            }
        }

        for (Map.Entry<Character, List<Point>> points : pointMap.entrySet()) {
            if (points.getValue().size() > 1) {
                for (int i = 0; i < points.getValue().size() - 1; i++) {
                    for (int j = i + 1; j < points.getValue().size(); j++) {
                        int pointFirstRow = points.getValue().get(i).x;
                        int pointFirstCol = points.getValue().get(i).y;
                        int pointSecondRow = points.getValue().get(j).x;
                        int pointSecondCol = points.getValue().get(j).y;
                        pointies.add(new Point(pointFirstRow, pointFirstCol));
                        pointies.add(new Point(pointSecondRow, pointSecondCol));

                        int rowDistance = pointFirstRow - pointSecondRow;
                        int colDistance = pointFirstCol - pointSecondCol;

                        boolean breaksUpBound = rowDistance + pointFirstRow < 0;
                        boolean breaksDownBound = (rowDistance * -1) + pointSecondRow > amountRows - 1;
                        boolean firstColLeft = colDistance < 0;
                        boolean firstColRight = colDistance > 0;
                        boolean breaksLeftBound = firstColLeft && colDistance + pointFirstCol < 0;
                        boolean breaksRightBound = firstColRight && colDistance + pointFirstCol > amountRows - 1;
                        boolean breaksLeftBoundLower = firstColRight && (colDistance * -1) + pointSecondCol < 0;
                        boolean breaksRightBoundLower = firstColLeft && (colDistance * -1) + pointSecondCol > amountCols - 1;

                        while (!breaksUpBound && !(breaksLeftBound || breaksRightBound)) {
                            pointFirstRow += rowDistance;
                            pointFirstCol += colDistance;
                            pointies.add(new Point(pointFirstRow, pointFirstCol));
                            breaksUpBound = rowDistance + pointFirstRow < 0;
                            breaksLeftBound = firstColLeft && colDistance + pointFirstCol < 0;
                            breaksRightBound = firstColRight && colDistance + pointFirstCol > amountRows - 1;
                        }
                        while (!breaksDownBound && !(breaksLeftBoundLower || breaksRightBoundLower)) {
                            pointSecondRow += (rowDistance * -1);
                            pointSecondCol += (colDistance * -1);
                            pointies.add(new Point(pointSecondRow, pointSecondCol));
                            breaksDownBound = (rowDistance * -1) + pointSecondRow > amountRows - 1;
                            breaksLeftBoundLower = firstColRight && (colDistance * -1) + pointSecondCol < 0;
                            breaksRightBoundLower = firstColLeft && (colDistance * -1) + pointSecondCol > amountCols - 1;
                        }
                    }
                }
            }
        }

        answer = pointies.stream().distinct().count();
        for (Point points : pointies) {
            stringList.get(points.x)[points.y] = '#';
        }
        for (char[] list : stringList) {
            System.out.println(list);
        }
        return answer;
    }

    public static long day8part1() throws Exception {
        long answer = 0L;
        List<char[]> stringList = Files.readAllLines(Paths.get("src/main/resources/day8test.txt")).stream()
                .map(String::toCharArray)
                .toList();
        HashMap<Character, List<Point>> pointMap = new HashMap<>();
        List<Point> pointies = new ArrayList<>();
        int amountRows = stringList.size();
        int amountCols = stringList.getFirst().length;
        for (int i = 0; i < amountRows; i++) {
            for (int j = 0; j < amountCols; j++) {
                char current = stringList.get(i)[j];
                if (current != '.') pointMap.computeIfAbsent(current, k -> new ArrayList<>()).add(new Point(i, j));
            }
        }

        for (Map.Entry<Character, List<Point>> points : pointMap.entrySet()) {
            if (points.getValue().size() > 1) {
                for (int i = 0; i < points.getValue().size() - 1; i++) {
                    for (int j = i + 1; j < points.getValue().size(); j++) {
                        int pointFirstRow = points.getValue().get(i).x;
                        int pointFirstCol = points.getValue().get(i).y;
                        int pointSecondRow = points.getValue().get(j).x;
                        int pointSecondCol = points.getValue().get(j).y;

                        int rowDistance = pointFirstRow - pointSecondRow;
                        int colDistance = pointFirstCol - pointSecondCol;

                        boolean breaksUpBound = rowDistance + pointFirstRow < 0;
                        boolean breaksDownBound = (rowDistance * -1) + pointSecondRow > amountRows - 1;
                        boolean firstColLeft = colDistance < 0;
                        boolean firstColRight = colDistance > 0;
                        boolean breaksLeftBound = firstColLeft && colDistance + pointFirstCol < 0;
                        boolean breaksRightBound = firstColRight && colDistance + pointFirstCol > amountRows - 1;
                        boolean breaksLeftBoundLower = firstColRight && (colDistance * -1) + pointSecondCol < 0;
                        boolean breaksRightBoundLower = firstColLeft && (colDistance * -1) + pointSecondCol > amountCols - 1;

                        if (!breaksUpBound) {
                            if (!(breaksLeftBound || breaksRightBound)) {
                                pointies.add(new Point(rowDistance + pointFirstRow, colDistance + pointFirstCol));
                            }
                        }
                        if (!breaksDownBound) {
                            if (!(breaksLeftBoundLower || breaksRightBoundLower)) {
                                pointies.add(new Point((rowDistance * -1) + pointSecondRow, (colDistance * -1) + pointSecondCol));
                            }
                        }
                    }
                }
            }
        }

        answer = pointies.stream().distinct().count();
        for (Point points : pointies) {
            stringList.get(points.x)[points.y] = '#';
        }
        for (char[] list : stringList) {
            System.out.println(list);
        }
        return answer;
    }

    public static long day7part1() throws Exception {
        long answer = 0L;
        File file = new File("src/main/resources/day7.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<String> stringList = new ArrayList<>();
        List<long[]> intList = new ArrayList<>();
        String st;
        while ((st = br.readLine()) != null) {
            stringList.add((st.replace(":", "")));
        }
        for (String line : stringList) {
            long[] numbers = Arrays.stream(line.split(" ")).mapToLong(Long::parseLong).toArray();
            intList.add(numbers);
        }
        for (long[] one : intList) {
            if (recursiveCheck(one[0], Arrays.stream(one).filter(e -> e != one[0]).toArray(), 0, 0)) answer += one[0];
        }
        System.out.println(stringList);

        return answer;
    }

    public static boolean recursiveCheck(long correct, long[] numsToCheck, long ans, int index) {
        if (correct == ans) return true;
        else if (index == numsToCheck.length) return false;
        long newAnsPLus = ans + numsToCheck[index];
        long newAnsMultiply = index == 0 ? numsToCheck[index] : ans * numsToCheck[index];
        long newAnsConcat = index == 0 ? numsToCheck[index] : parseLong(String.valueOf(ans) + String.valueOf(numsToCheck[index]));
        boolean possiblePlus = recursiveCheck(correct, numsToCheck, newAnsPLus, index + 1);
        boolean possibleMultiply = recursiveCheck(correct, numsToCheck, newAnsMultiply, index + 1);
        boolean possibleConcat = recursiveCheck(correct, numsToCheck, newAnsConcat, index + 1);
        return possiblePlus || possibleMultiply || possibleConcat;
    }


    public static long day5part2(List<int[]> failedList, HashMap<Integer, int[]> numPair) throws Exception {
        long answer = 0L;
        for (int[] ints : failedList) {
            for (int j = 0; j < ints.length; j++) {
                for (int k = 0; k < ints.length; k++) {
                    if (j < k) {
                        if (numPair.containsKey(ints[k])) {
                            int finalJ = j;
                            if (Arrays.stream(numPair.get(ints[k])).anyMatch((e) -> e == ints[finalJ])) {
                                int saveInt = ints[j];
                                ints[j] = ints[k];
                                ints[k] = saveInt;
                            }
                        }
                    } else if (j > k) {
                        if (numPair.containsKey(ints[j])) {
                            int finalK = k;
                            if (Arrays.stream(numPair.get(ints[j])).anyMatch((e) -> e == ints[finalK])) {
                                int saveInt = ints[k];
                                ints[k] = ints[j];
                                ints[j] = saveInt;
                            }
                        }
                    }
                }
            }
            answer += ints[ints.length / 2];
        }
        return answer;
    }


    public static long day5part1() throws Exception {
        long answer = 0L;
        File file = new File("src/main/resources/day5.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List<int[]> ruleList = new ArrayList<>();
        List<int[]> checkList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            if (line.contains("|")) {
                String[] parts = line.split("\\|");
                int[] array = new int[]{
                        Integer.parseInt(parts[0].trim()),
                        Integer.parseInt(parts[1].trim())
                };
                ruleList.add(array);
            } else if (line.contains(",")) {
                String[] parts = line.split(",");
                int[] array = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    array[i] = Integer.parseInt(parts[i].trim());
                }
                checkList.add(array);
            }
        }

        HashMap<Integer, int[]> numPair = new HashMap<>();
        for (int[] arr : ruleList) {
            if (numPair.containsKey(arr[0])) {
                int[] array = numPair.get(arr[0]);
                int[] newArray = Arrays.copyOf(array, array.length + 1);
                newArray[newArray.length - 1] = arr[1];
                numPair.replace(arr[0], newArray);
            } else {
                numPair.put(arr[0], new int[]{arr[1]});
            }
        }
        boolean badList = false;
        List<int[]> failedList = new ArrayList<>();
        for (int[] ints : checkList) {
            badList = false;
            for (int j = 0; j < ints.length; j++) {
                if (badList) break;
                for (int k = 0; k < ints.length; k++) {
                    if (j < k) {
                        if (numPair.containsKey(ints[k])) {
                            int finalJ = j;
                            if (Arrays.stream(numPair.get(ints[k])).anyMatch((e) -> e == ints[finalJ])) {
                                badList = true;
                                failedList.add(ints);
                                break;
                            }
                        }
                    } else if (j > k) {
                        if (numPair.containsKey(ints[j])) {
                            int finalK = k;
                            if (Arrays.stream(numPair.get(ints[j])).anyMatch((e) -> e == ints[finalK])) {
                                badList = true;
                                failedList.add(ints);
                                break;
                            }
                        }
                    }
                }
            }
            if (!badList) answer += ints[ints.length / 2];
        }
        System.out.println(answer);
        System.out.println(day5part2(failedList, numPair));
        return answer;
    }

    public static long day4part2() throws Exception {
        long answer = 0L;
        File file = new File("src/main/resources/day4.txt");
        BufferedReader br = new BufferedReader(new FileReader(file));
        List<String> stringList = new ArrayList<>();
        String st;
        while ((st = br.readLine()) != null) {
            stringList.add(st);
        }
        for (int i = 0; i < stringList.size(); i++) {
            for (int j = 0; j < stringList.get(i).length(); j++) {
                if (stringList.get(i).charAt(j) == 'A') {
                    answer += checkMatches2(stringList, i, j);
                }
            }

        }
        return answer;
    }

    public static int checkMatches2(List<String> xmasList, int i, int j) {
        int answer = 0;

        if (j >= 1 && i >= 1 && j <= xmasList.get(i).length() - 2 && i <= xmasList.size() - 2) {
            if (xmasList.get(i - 1).charAt(j - 1) == 'M' && xmasList.get(i + 1).charAt(j - 1) == 'M' && xmasList.get(i - 1).charAt(j + 1) == 'S' && xmasList.get(i + 1).charAt(j + 1) == 'S')
                answer += 1;
            if (xmasList.get(i - 1).charAt(j - 1) == 'M' && xmasList.get(i - 1).charAt(j + 1) == 'M' && xmasList.get(i + 1).charAt(j - 1) == 'S' && xmasList.get(i + 1).charAt(j + 1) == 'S')
                answer += 1;
            if (xmasList.get(i - 1).charAt(j + 1) == 'M' && xmasList.get(i + 1).charAt(j + 1) == 'M' && xmasList.get(i - 1).charAt(j - 1) == 'S' && xmasList.get(i + 1).charAt(j - 1) == 'S')
                answer += 1;
            if (xmasList.get(i + 1).charAt(j - 1) == 'M' && xmasList.get(i + 1).charAt(j + 1) == 'M' && xmasList.get(i - 1).charAt(j - 1) == 'S' && xmasList.get(i - 1).charAt(j + 1) == 'S')
                answer += 1;
        }
        return answer;
    }

    public static long day4part1() throws Exception {
        long answer = 0L;
        File file = new File("src/main/resources/day4.txt");
        BufferedReader br = new BufferedReader((new FileReader(file)));
        List<String> stringList = new ArrayList<>();
        String st;
        while ((st = br.readLine()) != null) {
            stringList.add(st);
        }
        for (int i = 0; i < stringList.size(); i++) {
            for (int j = 0; j < stringList.get(i).length(); j++) {
                if (stringList.get(i).charAt(j) == 'X') {
                    answer += checkMatches(stringList, i, j);
                }
            }
        }

        return answer;
    }

    public static int checkMatches(List<String> xmasList, int i, int j) {
        int answer = 0;
        if (j >= 3) {
            // left
            if (xmasList.get(i).charAt(j - 1) == 'M' && xmasList.get(i).charAt(j - 2) == 'A' && xmasList.get(i).charAt(j - 3) == 'S')
                answer += 1;
        }
        if (j >= 3 && i >= 3) {
            //diag up left
            if (xmasList.get(i - 1).charAt(j - 1) == 'M' && xmasList.get(i - 2).charAt(j - 2) == 'A' && xmasList.get(i - 3).charAt(j - 3) == 'S')
                answer += 1;
        }
        if (i >= 3) {
            // up
            if (xmasList.get(i - 1).charAt(j) == 'M' && xmasList.get(i - 2).charAt(j) == 'A' && xmasList.get(i - 3).charAt(j) == 'S')
                answer += 1;
        }
        if (i >= 3 && j <= xmasList.get(i).length() - 4) {
            // diag up right
            if (xmasList.get(i - 1).charAt(j + 1) == 'M' && xmasList.get(i - 2).charAt(j + 2) == 'A' && xmasList.get(i - 3).charAt(j + 3) == 'S')
                answer += 1;
        }
        if (j <= xmasList.get(i).length() - 4) {
            //right
            if (xmasList.get(i).charAt(j + 1) == 'M' && xmasList.get(i).charAt(j + 2) == 'A' && xmasList.get(i).charAt(j + 3) == 'S')
                answer += 1;
        }
        if (j <= xmasList.get(i).length() - 4 && i <= xmasList.get(i).length() - 4) {
            //diag down right
            if (xmasList.get(i + 1).charAt(j + 1) == 'M' && xmasList.get(i + 2).charAt(j + 2) == 'A' && xmasList.get(i + 3).charAt(j + 3) == 'S')
                answer += 1;
        }
        if (i <= xmasList.size() - 4) {
            //down
            if (xmasList.get(i + 1).charAt(j) == 'M' && xmasList.get(i + 2).charAt(j) == 'A' && xmasList.get(i + 3).charAt(j) == 'S')
                answer += 1;
        }
        if (i <= xmasList.size() - 4 && j >= 3) {
            //diag down left
            if (xmasList.get(i + 1).charAt(j - 1) == 'M' && xmasList.get(i + 2).charAt(j - 2) == 'A' && xmasList.get(i + 3).charAt(j - 3) == 'S')
                answer += 1;
        }
        return answer;
    }

    public static long day3part1() throws Exception {
        long answer = 0L;
        boolean count = true;
        List<String> mulList = fileRead("day3part1.txt");

        for (int i = 0; i < mulList.size(); i++) {
            if (mulList.get(i).contains("do()")) {
                count = true;
            } else if (mulList.get(i).contains("don't()")) {
                count = false;
            } else {
                if (count == true) {
                    int firstNumberStart = mulList.get(i).indexOf("(") + 1;
                    int firstNumberEnd = mulList.get(i).indexOf(",");
                    int secondNumberStart = mulList.get(i).indexOf(",") + 1;
                    int secondNumberEnd = mulList.get(i).indexOf(")");
                    int firstNumber = parseInt(mulList.get(i).substring(firstNumberStart, firstNumberEnd));
                    int secondNumber = parseInt(mulList.get(i).substring(secondNumberStart, secondNumberEnd));
                    answer += (long) firstNumber * secondNumber;
                }
            }
        }
        return answer;
    }

    public static List<String> fileRead(String txt) throws Exception {
        File file = new File("src/main/resources/" + txt);
        BufferedReader br = new BufferedReader((new FileReader(file)));
        List<String> stringList = new ArrayList<>();
        String st;
        Pattern r = Pattern.compile("[m][u][l][(]\\d+[,]\\d+[)]|[d][o][(][)]|[d][o][n]['][t][(][)]");
        Matcher m;
        while ((st = br.readLine()) != null) {
            m = r.matcher(st);
            while (m.find()) {
                stringList.add(m.group(0));
            }
        }
        return stringList;
    }

    public static long day1part2() throws Exception {
        long answer = 0;
        File file = new File("src/main/resources/input.txt");
        BufferedReader br = new BufferedReader((new FileReader(file)));
        String st;
        ArrayList<Integer> stringList1 = new ArrayList<>();
        ArrayList<Integer> stringList2 = new ArrayList<>();
        while ((st = br.readLine()) != null) {
            stringList1.add(Integer.valueOf(st.split("\\s+")[0]));
            stringList2.add(Integer.valueOf(st.split("\\s+")[1]));
        }
        stringList1.sort(Integer::compareTo);
        stringList2.sort(Integer::compareTo);
        HashMap<Integer, Integer> answerMap = new HashMap<>();
        for (int i = 0; i < stringList1.size(); i++) {
//            if (answerMap.containsKey(stringList1.get(i))) {
//                answerMap.replace(stringList1.get(i), answerMap.get(i)+1);
//            } else {
            answerMap.put(stringList1.get(i), 0);
//            }
        }
        for (int i = 0; i < stringList2.size(); i++) {
            if (answerMap.containsKey(stringList2.get(i))) {
                answerMap.replace(stringList2.get(i), answerMap.get(stringList2.get(i)) + 1);
            }
        }
        for (Map.Entry<Integer, Integer> entry : answerMap.entrySet()) {
            Integer key = entry.getKey();
            Integer value = entry.getValue();
            answer += (long) key * value;
        }

        return answer;
    }

    public Long day1part1() throws Exception {
        File file = new File("src/main/resources/input.txt");
        BufferedReader br = new BufferedReader((new FileReader(file)));
        String st;
        ArrayList<Integer> stringList1 = new ArrayList<>();
        ArrayList<Integer> stringList2 = new ArrayList<>();
        while ((st = br.readLine()) != null) {
            stringList1.add(Integer.valueOf(st.split("\\s+")[0]));
            stringList2.add(Integer.valueOf(st.split("\\s+")[1]));
        }
        stringList1.sort(Integer::compareTo);
        stringList2.sort(Integer::compareTo);
        Long answer = 0L;
        for (int i = 0; i < stringList1.size(); i++) {
            answer += Math.abs(stringList1.get(i) - stringList2.get(i));
        }
        return answer;
    }
}

