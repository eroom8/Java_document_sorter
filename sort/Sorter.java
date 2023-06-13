import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;
import java.util.Comparator;

class Entry {
    private final String name;
    private final Integer count;

    public Entry(String name, Integer count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public Integer getCount() {
        return count;
    }

    public String toString() {
        return String.format("%s,%d", this.name, this.count);
    }

    public static Entry[] fromFile(String fileName, int numEntries)
            throws IllegalArgumentException, FileNotFoundException {
        Entry[] entries = new Entry[numEntries];

        try (Scanner scanner = new Scanner(new File(fileName))) {
            int index = 0;
            while (scanner.hasNextLine() && index < numEntries) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String name = parts[0].trim();
                int count = Integer.parseInt(parts[1].trim());

                entries[index] = new Entry(name, count);
                index++;
            }
        }

        return entries;
    }

    public static void toFile(String fileName, Entry[] entries, int numEntries)
            throws IllegalArgumentException, FileNotFoundException {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (int i = 0; i < numEntries; i++) {
                writer.println(entries[i].toString());
            }
        }
    }
}

class NameComparator implements Comparator<Entry> {
    public int compare(Entry entry1, Entry entry2) {
        return entry1.getName().compareToIgnoreCase(entry2.getName());
    }
}

class CountComparator implements Comparator<Entry> {
    public int compare(Entry entry1, Entry entry2) {
        return entry1.getCount().compareTo(entry2.getCount());
    }
}

class NameThenCountComparator implements Comparator<Entry> {
    public int compare(Entry entry1, Entry entry2) {
        int nameComparison = entry1.getName().compareToIgnoreCase(entry2.getName());
        if (nameComparison != 0) {
            return nameComparison;
        }
        return entry1.getCount().compareTo(entry2.getCount());
    }
}

class CountThenNameComparator implements Comparator<Entry> {
    public int compare(Entry entry1, Entry entry2) {
        int countComparison = entry1.getCount().compareTo(entry2.getCount());
        if (countComparison != 0) {
            return countComparison;
        }
        return entry1.getName().compareToIgnoreCase(entry2.getName());
    }
}

public class Sorter {
    private void merge(Entry[] items, int left, int middle, int right, Comparator<Entry> comparator) {
        int n1 = middle - left + 1;
        int n2 = right - middle;

        Entry[] leftArr = new Entry[n1];
        Entry[] rightArr = new Entry[n2];

        System.arraycopy(items, left, leftArr, 0, n1);
        System.arraycopy(items, middle + 1, rightArr, 0, n2);

        int i = 0, j = 0;
        int k = left;

        while (i < n1 && j < n2) {
            if (comparator.compare(leftArr[i], rightArr[j]) <= 0) {
                items[k] = leftArr[i];
                i++;
            } else {
                items[k] = rightArr[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            items[k] = leftArr[i];
            i++;
            k++;
        }

        while (j < n2) {
            items[k] = rightArr[j];
            j++;
            k++;
        }
    }

    private void mergeSort(Entry[] items, int left, int right, Comparator<Entry> comparator) {
        if (left < right) {
            int middle = left + (right - left) / 2;

            mergeSort(items, left, middle, comparator);
            mergeSort(items, middle + 1, right, comparator);

            merge(items, left, middle, right, comparator);
        }
    }

    private Comparator<Entry> getComparator(String mode) throws IllegalArgumentException {
        switch (mode) {
            case "name":
                return new NameThenCountComparator(); // Modified to use NameThenCountComparator
            case "count":
                return new CountComparator();
            case "nameThenCount":
                return new NameThenCountComparator();
            case "countThenName":
                return new CountThenNameComparator();
            default:
                throw new IllegalArgumentException("Invalid mode: " + mode);
        }
    }

    public static void main(String[] args) throws Exception {
        String inputFileName = "input.txt";
        int numEntries = 20;
        String mode = "name";
        String outputFileName = "output.txt";

        try {
            Entry[] entries = Entry.fromFile(inputFileName, numEntries);

            Sorter sorter = new Sorter();
            Comparator<Entry> comparator = sorter.getComparator(mode);

            sorter.mergeSort(entries, 0, numEntries - 1, comparator);
            Entry.toFile(outputFileName, entries, numEntries);

            System.out.println("Sorting completed. The sorted data is stored in " + outputFileName);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}