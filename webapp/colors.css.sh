#!/bin/bash
C="0 3 6 9 c f"
for x in $C
do
	for y in $C
	do
		for z in $C
		do
			cat << EOF
.f$x$y$z{color:#$x$x$y$y$z$z}
.b$x$y$z{background:#$x$x$y$y$z$z}
EOF
		done
	done
done