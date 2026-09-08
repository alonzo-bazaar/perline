#!/usr/bin/env sh

# line numbers
ln () {
    perl -e 'my $l=0; while(<>){print "$l $_"; $l++;}' $1
}
