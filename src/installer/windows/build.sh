#!/bin/bash

# Copyright (c) 2024 Marelis Adlatus <software@marelis.cz>
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# script termination on error
set -e

# command line - verbosity
if [[ $1 == "--verbose" ]] ; then
    set -x
fi

app_name="RadioRec"
app_version="1.0"
app_desc="Application for playing and recording internet radio stations."
app_vendor="Marelis Adlatus"
app_copyright="Copyright (c) 2024 Marelis Adlatus <software@marelis.cz>"

main_jar="${app_name}-${app_version}.jar"
main_class="cz.marelis.radiorec.RadioRec"
java_options="--java-options '-Dhttps.protocols=TLSv1.2'"

build_dir="build"
runtime_dir="runtime"
temp_dir="temp"
release_dir="release"

clean () {
    echo "info: prepare build"
    rm -Rf "$temp_dir" "$runtime_dir" "$release_dir" "$app_name"
    rm -f modules-*
    mkdir "$release_dir"
}

get_used_modules () {
    echo "info: list of all jdk modules"
    java --list-modules | cut -f1 -d"@" | sed 's/^ *//;s/ *$//' | tr -d '\r' > modules-jdk
    #echo "info: finding the required modules with module-info"
    #jar --file="${build_dir}/${main_jar}" --describe-module | grep "requires" | cut -d' ' -f2 | tr -d '\r' > modules-app
    echo "info: finding the required modules"
    jdeps --multi-release base --module-path "${build_dir}/libs" --list-reduced-deps \
    --ignore-missing-deps "${build_dir}/${main_jar}" | sed 's/^ *//;s/ *$//' | tr -d '\r' > modules-app
    echo "info: required in jdk modules"
    grep -f modules-jdk modules-app > modules-list
    # comma-separated list
    modules=$(cat modules-list | paste -d',' -s)
}

build_java_runtime () {
    echo "info: build java runtime"
    manual_modules=",jdk.crypto.ec,jdk.localedata"
    jlink --no-header-files --no-man-pages --compress=2 --strip-debug \
    --add-modules $modules$manual_modules --include-locales=en,de --output "$runtime_dir"
}

build_app_image () {
    echo "info: build app image"
    jpackage --type app-image --name "$app_name" --app-version "$app_version" \
    --description "$app_desc" --vendor "$app_vendor" --copyright "$app_copyright" \
    --main-jar "$main_jar" --main-class "$main_class" $java_options \
    --icon "$app_icon" --input "$build_dir" --temp "$temp_dir" --runtime-image "$runtime_dir"
}

archive_app_image () {
    echo "info: archive app image"
    7z a -tzip -bso0 "${release_dir}/${app_name}-${app_version}-image.zip" ${app_name}
}

build_debian () {
    echo "info: build debian package"
    rm -Rf "$temp_dir"
    jpackage --type deb --name "$app_name" --app-version "$app_version" --license-file "addons/License.txt" \
    --description "$app_desc" --vendor "$app_vendor" --copyright "$app_copyright" \
    --main-jar "$main_jar" --main-class "$main_class" $java_options \
    --icon "$app_icon" --input "$build_dir" --temp "$temp_dir" --runtime-image "$runtime_dir" \
    --dest "${release_dir}" --file-associations ${app_name}.properties --linux-shortcut \
    --linux-menu-group "Audio;Network;Recorder"  --linux-deb-maintainer "marelis.software@gmail.com" \
    --linux-app-release "1" --linux-app-category "Sound" --linux-package-deps "chromium-browser"
}

build_rpm () {
    echo "info: build rpm package"
    rm -Rf "$temp_dir"
    jpackage --type rpm --name "$app_name" --app-version "$app_version" --license-file "addons/License.txt" \
    --description "$app_desc" --vendor "$app_vendor" --copyright "$app_copyright" \
    --main-jar "$main_jar" --main-class "$main_class" $java_options \
    --icon "$app_icon" --input "$build_dir" --temp "$temp_dir" --runtime-image "$runtime_dir" \
    --dest "${release_dir}" --file-associations ${app_name}.properties --linux-shortcut \
    --linux-menu-group "Audio;Network;Recorder" --linux-rpm-license-type "ASL 2.0" \
    --linux-app-release "1" --linux-app-category "Sound" --linux-package-deps "chromium-browser"
}

build_pkg () {
    echo "info: build pkg package"
    rm -Rf "$temp_dir"
    jpackage --type pkg --name "$app_name" --app-version "$app_version" --license-file "addons/License.txt" \
    --description "$app_desc" --vendor "$app_vendor" --copyright "$app_copyright" \
    --main-jar "$main_jar" --main-class "$main_class" $java_options \
    --icon "$app_icon" --input "$build_dir" --temp "$temp_dir" --runtime-image "$runtime_dir" \
    --dest "${release_dir}" --file-associations ${app_name}.properties --linux-shortcut \
    --linux-menu-group "Audio;Network;Recorder" --linux-app-release "1" \
    --linux-app-category "Sound" --linux-package-deps "chromium-browser"
}

set_permissions () {
    echo "info: set permissions"
    chmod -R 777 ./*
}

build_linux () {
    if [ -x "$(command -v apt)" ]; then
        build_debian
        elif [ -x "$(command -v zypper)" ]; then
        build_rpm
        elif [ -x "$(command -v yum)" ]; then
        build_rpm
        elif [ -x "$(command -v pkg)" ]; then
        build_pkg
        elif [ -x "$(command -v pacman)" ]; then
        echo "error: the pacman package manager is not supported"
    fi
}

build_inno_setup () {
    echo "info: build inno setup"
    cp "addons/License.txt" "$app_name/"
    cp "icons/Station.ico" "$app_name/"
    "/cygdrive/c/Program Files (x86)/Inno Setup 6/ISCC.exe" /q "${app_name}".iss
    mv *.exe "${release_dir}"
}

cd ".build/${app_name}/${app_version}"

echo "info: current dir" $(pwd)

system_name=$(uname)

if [[ "$system_name" == Linux* ]] ; then
    
    app_icon="icons/${app_name}-512.png"
    clean && get_used_modules && build_java_runtime && build_app_image \
    && archive_app_image && build_linux && set_permissions
    
elif [[ "$system_name" == Darwin* ]] ; then
    
    echo "Darwin ?"
    
elif [[ "$system_name" == CYGWIN* || "$system_name" == MINGW* ]] ; then
    
    PATH=$PATH:"/cygdrive/c/Program Files/Java/jdk-17/bin"
    app_icon="icons/${app_name}.ico"
    clean && get_used_modules && build_java_runtime && build_app_image \
    && archive_app_image && build_inno_setup && set_permissions
fi
