#!/usr/bin/env bqn
# tryna see if I can do better than align.pl as far as clarity and tersity go
# attempt 3: bqn

# lines
ls ← { 0=≠•args? •Flines "/dev/stdin" ; ∾´ •Flines¨ •args }
