#!/bin/bash

rm -f *.class
file=$(find -iregex ./resistancematcher.*)
MAXTIME="1.0"

if [ ! -f "$file" ]; then
    echo -e "Error: File '$file' not found.\nTest failed."
    exit 1
fi

file=${file:2}
num_right=0
total=0
line="________________________________________________________________________"
compiler=
interpreter=
language=
extension=${file##*.}
if [ "$extension" = "py" ]; then
    if [ ! -z "$PYTHON_PATH" ]; then
        interpreter=$(which python.exe)
    else
        interpreter=$(which python3.7)
    fi
    command="$interpreter $file"
    echo -e "Testing $file\n"
elif [ "$extension" = "java" ]; then
    language="java"
    command="java ${file%.java}"
    echo -n "Compiling $file..."
    javac $file
    echo -e "done\n"
elif [ "$extension" = "c" ] || [ "$extension" = "cpp" ]; then
    language="c"
    command="./${file%.*}"
    echo -n "Compiling $file..."
    results=$(make 2>&1)
    if [ $? -ne 0 ]; then
        echo -e "\n$results"
        exit 1
    fi
    echo -e "done\n"
fi

timeout() {
    time=$1

    # start the command in a subshell to avoid problem with pipes
    # (spawn accepts one command)
    cmd="/bin/bash -c \"$2\""

    expect -c "set echo \"-noecho\"; set timeout $time; spawn -noecho $cmd; expect timeout { exit 1 } eof { exit 0 }"    

    if [ $? -eq 1 ]; then
        return 0
    fi
    return 1
}

run_test_args() {
    (( ++total ))
    echo -n "Running test $total..."
    expected=$2
    local ismac=0
    date --version >/dev/null 2>&1
    if [ $? -ne 0 ]; then
       ismac=1
    fi
    local start=0
    if (( ismac )); then
        start=$(python -c 'import time; print time.time()')
    else
        start=$(date +%s.%N)
    fi
    if timeout $MAXTIME "$command $1 2>&1 | tr -d '\r' > tmp.txt"; then
        echo "failure [timed out after $MAXTIME seconds]"
    else
        received=$(cat tmp.txt)
        local end=$(date +%s.%N)
        if (( ismac )); then
            end=$(python -c 'import time; print time.time()')
        else
            end=$(date +%s.%N)
        fi
        local elapsed=$(echo "scale=3; $end - $start" | bc | awk '{printf "%.3f", $0}') 
        if [ "$expected" != "$received" ]; then
            echo -e "failure\n\nExpected$line\n$expected\n"
            echo -e "Received$line\n$received\n"
        else
            echo "success [$elapsed seconds]"
            (( ++num_right ))
        fi
    fi
    rm -f tmp.txt
}

if [ "$language" = "java" ]; then
    run_test_args "" "Usage: java ResistanceMatcher <target> <tolerance %> <num resistors> <input file>"
elif [ "$language" = "c" ]; then
    run_test_args "" "./resistancematcher <target> <tolerance %> <num resistors> <input file>"
else
    run_test_args "" "Usage: python3.7 resistancematcher <target> <tolerance %> <num resistors> <input file>"
fi

run_test_args "five 1.0 3 input_resistors.txt" "Error: Invalid target value 'five'."
run_test_args "-5 1.0 3 input_resistors.txt" "Error: Invalid target value '-5'."
run_test_args "0 1.0 3 input_resistors.txt" "Error: Invalid target value '0'."
run_test_args "5 one 3 input_resistors.txt" "Error: Invalid tolerance value 'one'."
run_test_args "5 -1 3 input_resistors.txt" "Error: Invalid tolerance value '-1'."
run_test_args "5 1 three input_resistors.txt" "Error: Invalid number of resistors 'three'."
run_test_args "5 1 -3 input_resistors.txt" "Error: Invalid number of resistors '-3'."
run_test_args "5 1 0 input_resistors.txt" "Error: Invalid number of resistors '0'."
run_test_args "5 1 3 notfound.txt" "Error: Input file 'notfound.txt' not found."

(cat << ENDOFTEXT
10
15
25
25
0
30
ENDOFTEXT
) > input_resistors.txt
run_test_args "5 1 3 input_resistors.txt" "Error: Invalid value '0' on line 5."

(cat << ENDOFTEXT
10
15
twenty
25
30
ENDOFTEXT
) > input_resistors.txt
run_test_args "5 1 3 input_resistors.txt" "Error: Invalid value 'twenty' on line 3."

(cat << ENDOFTEXT
1.1
2.7
3.8
5.0
7.5
ENDOFTEXT
) > input_resistors.txt
run_test_args "3.5 2 2 input_resistors.txt" "Max resistors in parallel: 2"$'\n'"Tolerance: 2.0 %"$'\n'"Target resistance of 3.5 ohms is not possible."
run_test_args "3.5 10 2 input_resistors.txt" "Max resistors in parallel: 2"$'\n'"Tolerance: 10.0 %"$'\n'"Target resistance of 3.5 ohms is possible with [7.5, 7.5] ohm resistors."$'\n'"Best fit: 3.7500 ohms"$'\n'"Percent error: 7.14 %"

(cat << ENDOFTEXT
0.68
1.1
2.7
3.8
5.0
7.5
8.2
9.1
10
ENDOFTEXT
) > input_resistors.txt
run_test_args "3.5 5 2 input_resistors.txt" "Max resistors in parallel: 2"$'\n'"Tolerance: 5.0 %"$'\n'"Target resistance of 3.5 ohms is possible with [5.0, 10.0] ohm resistors."$'\n'"Best fit: 3.3333 ohms"$'\n'"Percent error: 4.76 %"
run_test_args "1.5 2 3 input_resistors.txt" "Max resistors in parallel: 3"$'\n'"Tolerance: 2.0 %"$'\n'"Target resistance of 1.5 ohms is possible with [3.8, 5.0, 5.0] ohm resistors."$'\n'"Best fit: 1.5079 ohms"$'\n'"Percent error: 0.53 %"
run_test_args "1.5 2 10 input_resistors.txt" "Max resistors in parallel: 10"$'\n'"Tolerance: 2.0 %"$'\n'"Target resistance of 1.5 ohms is possible with [5.0, 5.0, 7.5, 7.5] ohm resistors."$'\n'"Best fit: 1.5000 ohms"$'\n'"Percent error: 0.00 %"

(cat << ENDOFTEXT
0.68
1.1
2.7
3.8
5.0
7.5
8.2
9.1
10
12.2
15.5
ENDOFTEXT
) > input_resistors.txt
run_test_args "3.5 0.1 10 input_resistors.txt" "Max resistors in parallel: 10"$'\n'"Tolerance: 0.1 %"$'\n'"Target resistance of 3.5 ohms is possible with [8.2, 12.2, 12.2] ohm resistors."$'\n'"Best fit: 3.4979 ohms"$'\n'"Percent error: 0.06 %"

(cat << ENDOFTEXT
25
22
20
18
15
10
9
8
7
2
1
ENDOFTEXT
) > input_resistors.txt
run_test_args "3.3 1 5 input_resistors.txt" "Max resistors in parallel: 5"$'\n'"Tolerance: 1.0 %"$'\n'"Target resistance of 3.3 ohms is possible with [9.0, 18.0, 22.0, 22.0, 22.0] ohm resistors."$'\n'"Best fit: 3.3000 ohms"$'\n'"Percent error: 0.00 %"

(cat << ENDOFTEXT
37
31
17
13
11
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
1
ENDOFTEXT
) > input_resistors.txt
run_test_args "3.497227535259405 0.1 10 input_resistors.txt" "Max resistors in parallel: 10"$'\n'"Tolerance: 0.1 %"$'\n'"Target resistance of 3.4972274 ohms is possible with [11.0, 13.0, 17.0, 31.0, 37.0] ohm resistors."$'\n'"Best fit: 3.4972 ohms"$'\n'"Percent error: 0.00 %"

rm -f input_resistors.txt

echo -e "\nTotal tests run: $total"
echo -e "Number correct : $num_right"
echo -n "Percent correct: "
echo "scale=2; 100 * $num_right / $total" | bc

if [ "$language" = "java" ]; then
    echo -e -n "\nRemoving class files..."
    rm -f *.class
    echo "done"
elif [ "$language" = "c" ]; then
    echo -e -n "\nCleaning project..."
    make clean > /dev/null 2>&1
    echo "done"
fi
