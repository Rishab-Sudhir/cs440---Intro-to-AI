#!/bin/bash

start_rate=1
end_rate=30
success_count=0

for rate in $(seq $start_rate $end_rate); do
    echo "Iteration: $rate"
    # Run the Java program and capture its output
    output=$(java -cp "./lib/*:." edu.cwru.sepia.Main2 data/labs/infexf/TwoUnitSmallMaze.xml)
    
    # Check if the output indicates a successful run
    echo "$output" | grep -q "win"
    if [ $? -eq 0 ]; then
        # Increment success count if this iteration was successful
        ((success_count++))
    fi

    wait
done

# Calculate success percentage
total_runs=$(($end_rate - $start_rate + 1))
success_percentage=$(echo "scale=2; ($success_count/$total_runs)*100" | bc)

echo "Total Runs: $total_runs, Successes: $success_count, Success Percentage: $success_percentage%"