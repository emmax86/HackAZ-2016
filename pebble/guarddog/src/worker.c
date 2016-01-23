
#include <pebble_worker.h>

#define SAMPLING_RATE ACCEL_SAMPLING_10HZ
#define SAMPLES_PER_CALLBACK 5
#define WORKER_TICKS 0

void accel_data_callback(AccelData * data, uint32_t num_samples)
{

    if (data->did_vibrate)
    {
	// TODO
	APP_LOG(APP_LOG_LEVEL_DEBUG, "The pebble vibrated");
    }

    int i = 0;
    for(;i < SAMPLES_PER_CALLBACK; i++)
    {
	APP_LOG(APP_LOG_LEVEL_DEBUG, "%d ", *(data+i));
    }
}

static void worker_init()
{
    accel_data_service_subscribe(SAMPLES_PER_CALLBACK, accel_data_callback);
    accel_service_set_sampling_rate(SAMPLING_RATE);
}

static void worker_deinit() {
    accel_data_service_unsubscribe();
}

int main(void)
{
    APP_LOG(APP_LOG_LEVEL_DEBUG, "Worker Init");
    worker_init();
    APP_LOG(APP_LOG_LEVEL_DEBUG, "Worker Event Loop");
    worker_event_loop();
    APP_LOG(APP_LOG_LEVEL_DEBUG, "Worker DeInit");
    worker_deinit();
}
