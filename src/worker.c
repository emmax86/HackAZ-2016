#include <pebble_worker.h>


void send_to_phone(uint16_t coor[]) {
  DataLoggingSessionRef logging_session = data_logging_create(0x1234, DATA_LOGGING_BYTE_ARRAY, 4, false);
  data_logging_log(logging_session, &coor, 3);
}
static void data_handler(AccelData *data, uint32_t samples) {

  // send the xyz to the foreground
  AppWorkerMessage msg_data = {
    .data0 = data[0].x,
    .data1 = data[0].y,
    .data2 = data[0].z,
  };

  // send to phone to be passed to server
  uint16_t coor[] = {data[0].x, data[0].y,data[0].z};
  send_to_phone(coor);
  // send to foreground app to detect disturbance
  app_worker_send_message(1, &msg_data);

}

void init() {
  // number of samples
  int num_samples = 1;
  accel_data_service_subscribe(num_samples, data_handler);
}

void deinit() {
  accel_data_service_unsubscribe();
}


int main() {
  init();
  worker_event_loop();
  deinit();
  return 0;
}
