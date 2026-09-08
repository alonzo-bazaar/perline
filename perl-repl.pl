#!/usr/bin/perl

print "perl> ";
while (<>) {
  chomp;
  print "=> ", eval, "\n";
  print "\nperl> ";
}
