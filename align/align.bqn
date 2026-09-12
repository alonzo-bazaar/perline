#!/usr/bin/env bqn
# tryna see if I can do better than align.pl as far as clarity and tersity go
# attempt 3: bqn

# whitespace chars (brutal approximation, first 33 (0-32) ascii chars)
ws ← @+↕33
# chars we're gonna to split by (can't find a regex engine, fuck)
ss ← ","∾ws
# split by
SB ← {(0≠≠¨)⊸/ (1-˜¬×1++`)∘(∊⟜𝕨)⊸⊔ 𝕩}
# strip trailing whitespace
STW←{1-×`⌾⌽∊⟜ws𝕩}⊸/

# lines
# (•Flines "/dev/stdin" is unix specific, there's no windows way to slurp stdin)
ls ← { 0=≠•args? •Flines "/dev/stdin" ; ∾´ •Flines¨ •args }

# split lines
sl←ss⊸SB¨ ls
sl↩>(⌈´≠¨sl)⊸↑¨sl # make split lines rectangular

# max lengths
ml←⌈˝≠¨˘sl
# pad aligned lines
pal←STW¨ <˘ (⊣∾" "∾⊢)´˘ (ml↑¨⊢)˘ sl
•Out¨ pal
