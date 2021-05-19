"use strict";

(() => {
  const drone = new Scaledrone(channelId);
  drone.on("error", (error) => {
    console.error("Error with connection:", error);
  });
  drone.on("close", (event) => {
    console.log("Connection closed:", event);
  });

  const commonRoom = drone.subscribe(commonRoomName);

  const nodes = {};
  commonRoom.on("message", async (message) => {
    console.log("common room on");

    const pc = new RTCPeerConnection(conf);
    const room = drone.subscribe(message.data.newWorker);
    room.on("message", async (message) => {
      if (message.data.answer) {
        const remoteDesc = new RTCSessionDescription(message.data.answer);
        await pc.setRemoteDescription(remoteDesc);
      }
    });
    const offer = await pc.createOffer();
    await pc.setLocalDescription(offer);
    drone.publish({
      room: message.data.newWorker,
      message: { offer },
    });
    pc.addEventListener("icecandidate", (event) => {
      if (event.candidate) {
        room.publish({
          room: message.data.newWorker,
          message: { newIceCandidate: event.candidate },
        });
      }
    });
    pc.addEventListener("connectionstatechange", (_) => {
      if (pc.connectionState === "connected") {
        console.log("Master connected to " + message.data.newWorker);
        nodes[message.data.newWorker] = pc;
      }
    });
  });

  async function run() {
    // TODO send data and function
    // TODO aggregate results
  }

  const runButton = document.getElementById("runButton");
  runButton.onclick = run;
})();
