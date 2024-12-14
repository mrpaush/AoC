import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws Exception {
        long start = System.nanoTime();
        long answer = day9part2();
        long end = System.nanoTime();

        System.out.println(answer);
        System.out.println("Finished in " + (end - start) / 1000000.0 + "ms");
    }

    public static long day9part2() throws Exception {
        long answer = 0L;
        String numbers = Files.readString(Paths.get("src/day9test.txt"));

        System.out.println(numbers);

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
        String numbers = Files.readString(Paths.get("src/day9.txt"));
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

    public static long day6part2() throws Exception {
        long answer = 0L;
        BufferedReader br = new BufferedReader(new FileReader(new File("src/day6.txt")));
        String line;
        List<char[]> originalMap = new ArrayList<>();
        int guardLine = 0;
        int guardIndex = 0;
        String guardDirection = "";
        while ((line = br.readLine()) != null) {
            if (line.contains("^")) {
                guardLine = originalMap.size();
                guardIndex = line.indexOf("^");
                guardDirection = "up";
            } else if (line.contains(">")) {
                guardLine = originalMap.size();
                guardIndex = line.indexOf(">");
                guardDirection = "right";
            } else if (line.contains("v")) {
                guardLine = originalMap.size();
                guardIndex = line.indexOf("v");
                guardDirection = "down";
            } else if (line.contains("<")) {
                guardLine = originalMap.size();
                guardIndex = line.indexOf("<");
                guardDirection = "left";
            }
            originalMap.add(line.toCharArray());
        }
        int originalGuardLine = guardLine;
        int originalGuardIndex = guardIndex;
        String originalGuardDirection = guardDirection;

        HashMap<String, String> collisions = new HashMap<>();

        for (int i = 0; i < originalMap.size(); i++) {
            for (int j = 0; j < originalMap.get(i).length; j++) {
                if ((i == originalGuardLine && j == originalGuardIndex) || originalMap.get(i)[j] == '#') continue;
                collisions.clear();
                char originalChar = originalMap.get(i)[j];
                guardLine = originalGuardLine;
                guardIndex = originalGuardIndex;
                guardDirection = originalGuardDirection;
                originalMap.get(i)[j] = '#';
                boolean escaped = false;
                boolean collision = false;

                while (!escaped && !collision) {
                    if (guardDirection == "up") {
                        if (guardLine == 0) {
                            originalMap.get(guardLine)[guardIndex] = 'X';
                            escaped = true;
                        } else {
                            if (originalMap.get(guardLine - 1)[guardIndex] != '#') {
                                originalMap.get(guardLine)[guardIndex] = 'X';
                                originalMap.get(guardLine - 1)[guardIndex] = '^';
                                guardLine--;
                            } else if (originalMap.get(guardLine - 1)[guardIndex] == '#') {
                                if (collisions.containsKey((guardLine - 1) + "," + guardIndex)) {
                                    if (collisions.get((guardLine - 1) + "," + guardIndex).contains("up")) {
                                        collision = true;
                                    } else {
                                        collisions.put((guardLine - 1) + "," + guardIndex, collisions.get((guardLine - 1) + "," + guardIndex)+" up");
                                    }
                                } else {
                                    collisions.put((guardLine - 1) + "," + guardIndex, "up");
                                }
                                guardDirection = "right";
                            }
                        }
                    }
                    if (guardDirection == "right") {
                        if (guardIndex == originalMap.get(guardLine).length - 1) {
                            originalMap.get(guardLine)[guardIndex] = 'X';
                            escaped = true;
                        } else {
                            if (originalMap.get(guardLine)[guardIndex + 1] != '#') {
                                originalMap.get(guardLine)[guardIndex] = 'X';
                                originalMap.get(guardLine)[guardIndex + 1] = '>';
                                guardIndex++;
                            } else if (originalMap.get(guardLine)[guardIndex + 1] == '#') {
                                if (collisions.containsKey(guardLine + "," + (guardIndex + 1))) {
                                    if (collisions.get(guardLine + "," + (guardIndex + 1)).contains("right")) {
                                        collision = true;
                                    } else {
                                        collisions.put(guardLine + "," + (guardIndex + 1), collisions.get(guardLine + "," + (guardIndex + 1))+" right");
                                    }
                                } else {
                                    collisions.put(guardLine + "," + (guardIndex + 1), "right");
                                }
                                guardDirection = "down";
                            }
                        }
                    }
                    if (guardDirection == "down") {
                        if (guardLine == originalMap.size() - 1) {
                            originalMap.get(guardLine)[guardIndex] = 'X';
                            escaped = true;
                        } else {
                            if (originalMap.get(guardLine + 1)[guardIndex] != '#') {
                                originalMap.get(guardLine)[guardIndex] = 'X';
                                originalMap.get(guardLine + 1)[guardIndex] = 'v';
                                guardLine++;
                            } else if (originalMap.get(guardLine + 1)[guardIndex] == '#') {
                                if (collisions.containsKey((guardLine + 1) + "," + guardIndex)) {
                                    if (collisions.get((guardLine + 1) + "," + guardIndex).contains("down")) {
                                        collision = true;
                                    } else {
                                        collisions.put((guardLine + 1) + "," + guardIndex, collisions.get((guardLine + 1) + "," + guardIndex)+" down");
                                    }
                                } else {
                                    collisions.put((guardLine + 1) + "," + guardIndex, "down");
                                }
                                guardDirection = "left";
                            }
                        }
                    }
                    if (guardDirection == "left") {
                        if (guardIndex == 0) {
                            originalMap.get(guardLine)[guardIndex] = 'X';
                            escaped = true;
                        } else {
                            if (originalMap.get(guardLine)[guardIndex - 1] != '#') {
                                originalMap.get(guardLine)[guardIndex] = 'X';
                                originalMap.get(guardLine)[guardIndex - 1] = '<';
                                guardIndex--;
                            } else if (originalMap.get(guardLine)[guardIndex - 1] == '#') {
                                if (collisions.containsKey(guardLine + "," + (guardIndex - 1))) {
                                    if (collisions.get(guardLine + "," + (guardIndex - 1)).contains("left")) {
                                        collision = true;
                                    } else {
                                        collisions.put(guardLine + "," + (guardIndex - 1), collisions.get(guardLine + "," + (guardIndex - 1))+" left");
                                    }
                                } else {
                                    collisions.put(guardLine + "," + (guardIndex - 1), "left");
                                }
                                guardDirection = "up";
                            }
                        }
                    }
                }
                if (collision && !escaped) answer++;
                originalMap.get(i)[j] = originalChar;
            }
        }
        return answer;
    }

    public static long day6part1() throws Exception {
        long answer = 0L;
        BufferedReader br = new BufferedReader(new FileReader(new File("src/day6.txt")));
        String line;
        List<char[]> originalMap = new ArrayList<>();
        int guardLine = 0;
        int guardIndex = 0;
        String guardDirection = "";
        while ((line = br.readLine()) != null) {
            if (line.contains("^")) {
                guardLine = originalMap.size();
                guardIndex = line.indexOf("^");
                guardDirection = "up";
            } else if (line.contains(">")) {
                guardLine = originalMap.size();
                guardIndex = line.indexOf(">");
                guardDirection = "right";
            } else if (line.contains("v")) {
                guardLine = originalMap.size();
                guardIndex = line.indexOf("v");
                guardDirection = "down";
            } else if (line.contains("<")) {
                guardLine = originalMap.size();
                guardIndex = line.indexOf("<");
                guardDirection = "left";
            }
            originalMap.add(line.toCharArray());
        }
        originalMap.get(guardLine)[guardIndex] = 'X';
        boolean escaped = false;
        while (!escaped) {
            if (guardDirection == "up") {
                if (guardLine == 0) {
                    originalMap.get(guardLine)[guardIndex] = 'X';
                    escaped = true;
                } else {
                    if (originalMap.get(guardLine - 1)[guardIndex] != '#') {
                        originalMap.get(guardLine)[guardIndex] = 'X';
                        originalMap.get(guardLine - 1)[guardIndex] = '^';
                        guardLine--;
                    } else if (originalMap.get(guardLine - 1)[guardIndex] == '#') {
                        guardDirection = "right";
                    }
                }
            }
            if (guardDirection == "right") {
                if (guardIndex == originalMap.get(guardLine).length - 1) {
                    originalMap.get(guardLine)[guardIndex] = 'X';
                    escaped = true;
                } else {
                    if (originalMap.get(guardLine)[guardIndex + 1] != '#') {
                        originalMap.get(guardLine)[guardIndex] = 'X';
                        originalMap.get(guardLine)[guardIndex + 1] = '>';
                        guardIndex++;
                    } else if (originalMap.get(guardLine)[guardIndex + 1] == '#') {
                        guardDirection = "down";
                    }
                }
            }
            if (guardDirection == "down") {
                if (guardLine == originalMap.size() - 1) {
                    originalMap.get(guardLine)[guardIndex] = 'X';
                    escaped = true;
                } else {
                    if (originalMap.get(guardLine + 1)[guardIndex] != '#') {
                        originalMap.get(guardLine)[guardIndex] = 'X';
                        originalMap.get(guardLine + 1)[guardIndex] = 'v';
                        guardLine++;
                    } else if (originalMap.get(guardLine + 1)[guardIndex] == '#') {
                        guardDirection = "left";
                    }
                }
            }
            if (guardDirection == "left") {
                if (guardIndex == 0) {
                    originalMap.get(guardLine)[guardIndex] = 'X';
                    escaped = true;
                } else {
                    if (originalMap.get(guardLine)[guardIndex - 1] != '#') {
                        originalMap.get(guardLine)[guardIndex] = 'X';
                        originalMap.get(guardLine)[guardIndex - 1] = '<';
                        guardIndex--;
                    } else if (originalMap.get(guardLine)[guardIndex - 1] == '#') {
                        guardDirection = "up";
                    }
                }
            }
        }
        for (int i = 0; i < originalMap.size(); i++) {
            for (int j = 0; j < originalMap.get(i).length; j++) {
                if (originalMap.get(i)[j] == 'X') answer++;
            }
        }
        return answer;
    }


    public static long day4part2() throws Exception {
        long answer = 0L;
        File file = new File("src/day4.txt");
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
        File file = new File("src/day4.txt");
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

    public static long day5part1() throws Exception {
        String fileName = "src/day5.txt";
        long answer = 0L;
        File file = new File(fileName);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        List<String> listNumbers = new ArrayList<>();
        HashMap<Integer, Integer[]> keyMap = new HashMap<>();

        while ((st = br.readLine()) != null) {
            listNumbers.add(st);
        }
        for (int i = 0; i < listNumbers.size(); i++) {
            if (listNumbers.get(i).contains("|")) {

            }
        }
        System.out.println(listNumbers);
        return answer;
    }

    public static long day2part2() throws Exception {
        String filepath = "src/input.txt";
        File file = new File(filepath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List<List<Integer>> numberList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            numberList.add(Arrays.stream((line.split("\\s"))).map(Integer::parseInt).collect(Collectors.toList()));
        }

        AtomicLong answer = new AtomicLong(0L);
        numberList.forEach((report) -> {
            boolean addTo = true;
            boolean newCheck = false;
            if (!Objects.equals(report.get(0), report.get(1))) {
                boolean asc = report.get(1) > report.get(0);
                for (int i = 1; i < report.size(); i++) {
                    if (asc && (report.get(i) > report.get(i - 1)) && (report.get(i) - report.get(i - 1)) <= 3) {

                    } else if (!asc && (report.get(i - 1) > report.get(i)) && (report.get(i - 1) - report.get(i) <= 3)) {

                    } else {
                        for (int j = 0; j < report.size(); j++) {
                            if (!newCheck) newCheck = checkSafeReports(report, j);
                            if (newCheck) break;
                        }
                        if (!newCheck) addTo = false;
                    }
                }
                if (addTo) answer.addAndGet(1);
            } else {
                if (checkSafeReports(report, 0)) answer.addAndGet(1);
            }
        });
        return answer.get();
    }

    public static boolean checkSafeReports(List<Integer> report, int ind) {
        List<Integer> newReport = new ArrayList<>(report);
        newReport.remove(ind);
        System.out.println(newReport);
        boolean addTo = true;
        if (!Objects.equals(newReport.get(0), newReport.get(1))) {
            boolean asc = newReport.get(1) > newReport.get(0);
            for (int i = 1; i < newReport.size(); i++) {
                if (asc && (newReport.get(i) > newReport.get(i - 1)) && (newReport.get(i) - newReport.get(i - 1)) <= 3) {

                } else if (!asc && (newReport.get(i - 1) > newReport.get(i)) && (newReport.get(i - 1) - newReport.get(i) <= 3)) {

                } else {
                    addTo = false;
                }
            }
        } else addTo = false;
        return addTo;
    }


    public static long day2part1() throws Exception {
        String filepath = "src/input.txt";
        File file = new File(filepath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;
        List<List<Integer>> numberList = new ArrayList<>();
        while ((line = br.readLine()) != null) {
            numberList.add(Arrays.stream((line.split("\\s"))).map(Integer::parseInt).toList());
        }
        AtomicLong answer = new AtomicLong(0L);
        numberList.forEach((report) -> {
            boolean addTo = true;
            if (report.get(0) != report.get(1)) {
                boolean asc = report.get(1) > report.get(0);
                for (int i = 1; i < report.size(); i++) {
                    if (asc && (report.get(i) > report.get(i - 1)) && (report.get(i) - report.get(i - 1)) <= 3) {

                    } else if (!asc && (report.get(i - 1) > report.get(i)) && (report.get(i - 1) - report.get(i) <= 3)) {

                    } else {
                        addTo = false;
                    }
                }
                if (addTo) answer.addAndGet(1);
            }
        });
        return answer.get();
    }
}