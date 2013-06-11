/*
 ============================================================================
 Name        : impulsListener.c
 Author      : Marco Descher
 Version     :
 Copyright   : Eclipse Public License
 Description :
 ============================================================================
 */

#include <signal.h>
#include <poll.h>
#include <string.h>
#include <signal.h>
#include <stdio.h>
#include <sys/fcntl.h>
#include <sys/signal.h>
#include <unistd.h>
#include <time.h>

#define GPIO_FN_MAXLEN	32
#define POLL_TIMEOUT	-1
#define RDBUF_LEN	5

sig_atomic_t impulsCount = 0;
sig_atomic_t timeCount = 0;
time_t periodStartTime;
time_t bufferTime;
int watt = -1;

void resetCounter(int signal_number) {
	impulsCount = 0;
	timeCount = 0;
	periodStartTime = time(NULL);
	watt = -1;
	printf("[OK] reset\n");
}

void statusOutput(int signal_number) {
	time_t nowTime = time(NULL);
	time_t elapsedTime = nowTime - periodStartTime;
	printf("%d/%d/%d\n", impulsCount, elapsedTime, watt);
}

void initializeSignalHandlers() {
	struct sigaction sa_resetCounter;
	memset(&sa_resetCounter, 0, sizeof(sa_resetCounter));
	sa_resetCounter.sa_handler = &resetCounter;
	sigaction(SIGUSR1, &sa_resetCounter, NULL);

	struct sigaction sa_statusOutput;
	memset(&sa_statusOutput, 0, sizeof(sa_statusOutput));
	sa_statusOutput.sa_handler = &statusOutput;
	sigaction(SIGUSR2, &sa_statusOutput, NULL);
}

int main(int argc, char **argv) {
	if (argc != 2) {
		printf(
				"Usage: %s <GPIO>\n\t- GPIO must be exported to sysfs and have enabled edge detection\n",
				argv[0]);
		return 1;
	}

	char fn[GPIO_FN_MAXLEN];
	int fd, ret;
	struct pollfd pfd;
	char rdbuf[RDBUF_LEN];

	periodStartTime = time(NULL);

	memset(rdbuf, 0x00, RDBUF_LEN);
	memset(fn, 0x00, GPIO_FN_MAXLEN);

	initializeSignalHandlers();

	printf("Process ID is %d\n", (int) getpid());

	snprintf(fn, GPIO_FN_MAXLEN-1, "/sys/class/gpio/gpio%s/value", argv[1]);

	fd = open(fn, O_RDONLY);
	if (fd < 0) {
		perror(fn);
		return 2;
	}
	pfd.fd = fd;
	pfd.events = POLLPRI | POLLERR;

	ret = read(fd, rdbuf, RDBUF_LEN - 1);
	if (ret < 0) {
		perror("read()");
		return 4;
	}

	while (1) {
		memset(rdbuf, 0x00, RDBUF_LEN);
		lseek(fd, 0, SEEK_SET);
		ret = poll(&pfd, POLLIN, POLL_TIMEOUT);
		if (ret == -1) {
			// occurs on signal handling
			continue;
		}
		if (ret < 0) {
			perror("poll()");
			close(fd);
			return 3;
		}
		if (ret == 0) {
			printf("timeout\n");
			continue;
		}
		ret = read(fd, rdbuf, RDBUF_LEN - 1);

		if (*rdbuf == '0') {
			impulsCount++;
			time_t nowTime = time(NULL);
			if(impulsCount>1) {
				watt = (3600/(nowTime-bufferTime));
			} else {
				watt = (3600/(nowTime-periodStartTime))*-1;
			}
			bufferTime = time(NULL);
		}

		if (ret < 0) {
			perror("read()");
			return 4;
		}
	}
	close(fd);
	return 0;
}
