#!/usr/bin/env perl
use strict;
use warnings;
use List::Util qw( min max );

my $OUTPUT_FIELD_SEPARATOR = " ";
my $OUTPUT_LINE_SEPARATOR  = "\n";
my $PADDING_ELEMENT        = " ";

sub pad_to {
    my $str = $_[0];
    my $tar_len = $_[1];
    my $pad = $PADDING_ELEMENT x max($tar_len - (length $str), 0);
    return "${str}${pad}";
}

my @lines = map { my @line = split /[,\s]+/; foreach(@line) { chomp ; };
                  \@line } <>;

my @lengths = map { my @a = map(length, @{$_}); \@a; } @lines;
my $max_line_fields = max (map {0+@{$_}} @lines);
my @max_field_widths = map {
    my $n = $_ ;
    max (map {@{$_} > $n ? @{$_}[$n] : 0} @lengths);
} (0...$max_line_fields-1);

foreach (@lines) {
    my @line = @{$_};
    foreach (0..$#line-1) {
        print pad_to($line[$_], $max_field_widths[$_]);
        print $OUTPUT_FIELD_SEPARATOR;
    }
    print pad_to($line[$#line], $max_field_widths[$#line]);
    print $OUTPUT_LINE_SEPARATOR;
}
