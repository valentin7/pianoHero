$(document).ready(function(){
	$("#form").on("submit", function(e) {
		e.preventDefault();
		var title = $("#songTitle").val();
		var imageFile = e.currentTarget.files[0];
		console.log(imageFile);
		var songFile = $("#songMp3").val();

		var postParameters = {
			songTitle: title,
			mp3Path: songFile.toJSON(),
			imagePath: imageFile.toJSON()
		};

		$.post("/storesong", postParameters);
	})


	$("#submitButton").bind("click", function(e) {
		e.preventDefault();
		var title = $("#songTitle").val();
		var imageFile = e.currentTarget.files[0];
		console.log(imageFile);
		var songFile = $("#songMp3").val();

		var postParameters = {
			songTitle: title,
			mp3Path: songFile.toJSON(),
			imagePath: imageFile.toJSON()
		};

		$.post("/storesong", postParameters);
	});
});