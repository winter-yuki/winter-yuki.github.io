"use strict";

let workerPC;

(() => {
  const drone = new Scaledrone(channelId);
  drone.on("error", (error) => {
    console.error("Error with connection:", error);
  });
  drone.on("close", (event) => {
    console.log("Connection closed:", event);
  });

  function setWorker() {
    console.log("set worker");
    const workerNameInput = document.getElementById("workerName");
    const workerName = workerNameInput.value;
    drone.publish({
      room: commonRoomName,
      message: { newWorker: workerName },
    });

    const room = drone.subscribe(workerName);
    workerPC = new RTCPeerConnection(conf);
    room.on("message", async (message) => {
      if (message.data.offer) {
        const desc = new RTCSessionDescription(message.data.offer);
        workerPC.setRemoteDescription(desc);
        const answer = await workerPC.createAnswer();
        await workerPC.setLocalDescription(answer);
        drone.publish({
          room: workerName,
          message: { answer },
        });
      }
      if (message.data.newIceCandidate) {
        try {
          await workerPC.addIceCandidate(message.data.newIceCandidate);
        } catch (e) {
          console.error("Error adding received ice candidate", e);
        }
      }
    });

    // TODO wasm
  }

  const setWorkerButton = document.getElementById("setWorkerButton");
  setWorkerButton.onclick = setWorker;
})();
