#include <pebble_worker.h>



static void data_handler(AccelData *data, uint32_t samples) {

  // constract a data packe
  AppWorkerMessage msg_data = {
    .data0 = data[0].x,
    .data1 = data[0].y,
    .data2 = data[0].z,
  };

  // send to phone to be passed to server
 // send_to_phone(data);
  
  // send to foreground app to detect disturbance
  app_worker_send_message(1,&msg_data);
  
}

void init() {
  // number of samples 
  int samples = 1;
  accel_data_service_subscribe(samples,data_handler);

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