var easyMDE = new EasyMDE({
    element: document.getElementById('markdownDescriptionEditor'),
    toolbar: ['bold', 'italic', 'strikethrough', 'heading', '|', 'quote', 'code' ,'unordered-list', 'ordered-list', '|', 'link', 'image', '|', 'preview', 'guide'],
});

var addComment = new EasyMDE({
    element: document.getElementById('markdownCommentEditor'),
    toolbar: ['bold', 'italic', 'strikethrough', 'heading', '|', 'quote', 'code' ,'unordered-list', 'ordered-list', '|', 'link', 'image', '|', 'preview', 'guide'],
    minHeight: "150px"
});

// this allows creation of a Markdown Editor for each comment in an Issue
for(let i=0; i < document.getElementsByClassName("comments").length; i++) {
    var editComment = new EasyMDE({
        element: document.getElementsByClassName('comments').item(i),
        toolbar: ['bold', 'italic', 'strikethrough', 'heading', '|', 'quote', 'code' ,'unordered-list', 'ordered-list', '|', 'link', 'image', '|', 'preview', 'guide'],
        minHeight: "150px"
    });
}