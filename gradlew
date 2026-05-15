#!/bin/sh

APP_HOME=$(cd "$(dirname "$0")" >/dev/null 2>&1 && pwd -P)
LOCAL_GRADLE="$APP_HOME/.gradle-home/wrapper/dists/gradle-7.3.3-bin/6a41zxkdtcxs8rphpq6y0069z/gradle-7.3.3/bin/gradle"

if [ -x "$LOCAL_GRADLE" ]; then
  exec "$LOCAL_GRADLE" "$@"
fi

JAVA_EXE="${JAVA_HOME:-}/bin/java"

if [ ! -x "$JAVA_EXE" ]; then
  JAVA_EXE=java
fi

exec "$JAVA_EXE" -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain "$@"
