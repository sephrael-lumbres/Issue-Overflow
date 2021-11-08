var easyMDE = new EasyMDE({
    element: document.getElementById('markdownEditor'),
    toolbar: ['bold', 'italic', 'strikethrough', 'heading', '|', 'quote', 'code' ,'unordered-list', 'ordered-list', '|', 'link', 'image', '|', 'preview', 'guide'],
});

var addComment = new EasyMDE({
    element: document.getElementById('commentEditor'),
    toolbar: ['bold', 'italic', 'strikethrough', 'heading', '|', 'quote', 'code' ,'unordered-list', 'ordered-list', '|', 'link', 'image', '|', 'preview', 'guide'],
    minHeight: "150px"
});