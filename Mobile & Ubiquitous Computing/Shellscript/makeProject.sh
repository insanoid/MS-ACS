#!/bin/sh

echo "Enter Target:"
read device_id
echo "Enter Project Name:"
read project_name
echo "Enter Path:"
read path
echo "Enter Activity Name:"
read activity_name
echo "Enter Package Name:"
read package

~/Code/android-sdk/tools/android create project --target $device_id --name $project_name --path $path --activity $activity_name --package $package
echo "#!/bin/sh\n ant build" > $path."/buildscript.sh"
chmod 777 $path"/buildscript.sh"
open $path

