/*
 ============================================================================
 Name        : impulsListener.c
 Author      : Marco Descher
 Version     :
 Copyright   : Eclipse Public License
 Description :
 Caveats	 : Messung mit Sekunden-Granularität limitiert auf <3.6kW/h
 	 	 	   Time-Out nicht definiert, power-off kann nicht detektiert werden
 ============================================================================
 */

#include <signal.h>
#include <poll.h>
#include <pthread.h>
#include <signal.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/fcntl.h>
#include <sys/signal.h>
#include <time.h>
#include <unistd.h>

#define GPIO_FN_MAXLEN	32
#define POLL_TIMEOUT	-1
#define RDBUF_LEN	5

sig_atomic_t impulsCount = 0;
sig_atomic_t timeCount = 0;
time_t periodStartTime;
time_t bufferTime;
pthread_t timerThread;
FILE *statusFile;
int gpio_file_descriptor;
char* filename;
int watt = -1;

void resetCounter(int signal_number) {
	impulsCount = 0;
	timeCount = 0;
	periodStartTime = time(NULL);
	watt = -1;
	printf("[OK] reset\n");
}

void terminateHandler() {
	close(gpio_file_descriptor);
	remove(filename);
}

void *thread_doOutput (int *gpio_port) {
	while(1) {
		statusFile = fopen(filename, "w");
		time_t nowTime = time(NULL);
		time_t elapsedTime = nowTime - periodStartTime;
		fprintf(statusFile, "%d/%d/%d", impulsCount, elapsedTime, watt);
		fflush(statusFile);
		fclose(statusFile);
		sleep(1);
	}
	return NULL;
}

void initializeSignalHandlers() {
	struct sigaction sa_resetCounter;
	memset(&sa_resetCounter, 0, sizeof(sa_resetCounter));
	sa_resetCounter.sa_handler = &resetCounter;
	sigaction(SIGUSR1, &sa_resetCounter, NULL);

	struct sigaction sa_terminateHandler;
	memset(&sa_terminateHandler, 0, sizeof(sa_terminateHandler));
	sa_terminateHandler.sa_handler = &terminateHandler;
	sigaction(SIGINT, &sa_terminateHandler, NULL);
}

int main(int argc, char **argv) {
	if (argc != 2) {
		printf(
				"Usage: %s <GPIO>\n\t- GPIO must be exported to sysfs and have enabled edge detection\n",
				argv[0]);
		return 1;
	}

	filename = (char*)malloc(32);
	char fn[GPIO_FN_MAXLEN];
	int ret;
	struct pollfd pfd;
	char rdbuf[RDBUF_LEN];
	int gpio_port = atoi(argv[1]);

	periodStartTime = time(NULL);

	memset(rdbuf, 0x00, RDBUF_LEN);
	memset(fn, 0x00, GPIO_FN_MAXLEN);

	initializeSignalHandlers();
	// copy filename into char *filename
	sprintf(filename, "%d_%d", (int) getpid(), (int) gpio_port);
	// create periodic update thread
	pthread_create(&timerThread, NULL, thread_doOutput, NULL);

	snprintf(fn, GPIO_FN_MAXLEN-1, "/sys/class/gpio/gpio%d/value", gpio_port);

	gpio_file_descriptor = open(fn, O_RDONLY);
	if (gpio_file_descriptor < 0) {
		perror(fn);
		return 2;
	}
	pfd.fd = gpio_file_descriptor;
	pfd.events = POLLPRI | POLLERR;

	ret = read(gpio_file_descriptor, rdbuf, RDBUF_LEN - 1);
	if (ret < 0) {
		perror("read()");
		return 4;
	}

	while (1) {
		memset(rdbuf, 0x00, RDBUF_LEN);
		lseek(gpio_file_descriptor, 0, SEEK_SET);
		ret = poll(&pfd, POLLIN, POLL_TIMEOUT);
		if (ret == -1) {
			// occurs on signal handling
			continue;
		}
		if (ret < 0) {
			perror("poll()");
			close(gpio_file_descriptor);
			return 3;
		}
		if (ret == 0) {
			printf("timeout\n");
			continue;
		}
		ret = read(gpio_file_descriptor, rdbuf, RDBUF_LEN - 1);

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
			// Do not output on SIGINT
			//perror("read()");
			return 4;
		}
	}



	close(gpio_file_descriptor);
	return 0;
}
