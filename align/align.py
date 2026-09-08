#!/usr/bin/env python
# tryna see if I can do better than align.pl as far as clarity and tersity go
# attempt 2: python
import sys, re

lines=[]
if len(sys.argv) != 1:
    for fn in sys.argv[1:]:
        with open(fn) as f:
            for line in f:
                lines.append(line.strip())
else:
    lines = map(str.strip, sys.stdin.readlines())

lines = [re.split(r'[,\s]+', line) for line in lines]
line_field_lengths=[[len(elt) for elt in line] for line in lines]
max_line_fields=max(map(len, lines))
column_max_lengths=[]
for i in line_field_lengths:
    ml = max(len(column_max_lengths), len(i))
    column_max_lengths += [0 for _ in range(ml - len(column_max_lengths))]
    i += [0 for _ in range(ml - len(i))]
    column_max_lengths = [max(x,y) for (x,y) in zip(column_max_lengths, i)]

for l in lines:
    padded = [a + (' ' * (b-len(a))) for (a, b) in zip (l, column_max_lengths)]
    print(' '.join(padded))
