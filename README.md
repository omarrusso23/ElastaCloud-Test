# Warning
The csv has been reduce to 5330 entries for the sake of time for the program to execute and the size of the file

# Data Loader

## Table of Contents  
1. [Introduction](#introduction)  
2. [Explanation of  the code](#explanation-of-the-code)  
3. [Crib Sheet](#crib-sheet)
4. [Usage](#usage)
5. [Output](#output)

## Introduction

This is a Java program that reads data from a CSV file and performs various operations on the data. The program is designed to read a specific CSV file and extract information from specific columns in the file. It then calculates and outputs various statistics based on the extracted data.

## Explanation of the code

The program uses the java.io and java.util packages to read and process the data from the CSV file. The program has a main method, which is the entry point of the program, and several helper methods that are used to process the data.

The DATA_FILE constant is used to specify the path of the CSV file. The COLUMN_INDEX_BY_NAME constant is a map that contains the index of the columns that the program is interested in.

The program reads the CSV file using a BufferedReader. The program skips the first line of the file, which contains the headers. The program then processes each line of the file and extracts the necessary information from the columns specified in COLUMN_INDEX_BY_NAME. The extracted information is then printed to the console.

The program also calculates various statistics based on the extracted data. The program uses HashMap and ArrayList to store the data and calculate the statistics. The program calculates the number of fines by make and year, the number of fines by agency and year, and the total fine amounts by agency and year. The program then prints the statistics to the console.

## Crib Sheet

We have a csv called crib sheet where we have 100 Agencies and we read it to extract the names and checked if the ids are in the data.csv

## Usage

To use this program, simply run the `DataLoader` class. The program will read in the `data.csv` and `crib sheet.csv` files located in the same directory as the program.

## Output

For each row in the CSV file, the program will output the following information:

- `Ticket Number`
- `Issue Date and Time`
- `Plate Expiry Date`
- `Latitude`
- `Longitude`
- `Agency Id`
- `Agency name`


In addition, the program calculates and outputs the following statistics:

- Total of fines issues per year per make of vehicle
- Average and Standard Deviation of the fine amount per year per agency
