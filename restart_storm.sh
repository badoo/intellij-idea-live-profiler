#!/bin/sh

if which phpstorm > /dev/null; then
  # launchbox-generated script
  echo "Using Lanuchbox-generated runner: phpstorm"
  STORM_RUNNER=phpstorm
elif which pstorm > /dev/null; then
  # case where PhpStorm created its run script
  echo "Using PhpStorm-generated runner: pstorm"
  STORM_RUNNER=pstorm
else
  echo "Can't find PhpStorm runner. Please create a launch script via Toolbox or 'Tools -> Create Command-line Launcher..."
  exit 1
fi

for i in {1..10}; do
   ps x | fgrep 'MacOS/phpstorm' | fgrep -vw fgrep >/dev/null
   if [ "g$?" = "g0" ]; then
       killall phpstorm
       sleep 1
   fi
done

$STORM_RUNNER

exit 0
