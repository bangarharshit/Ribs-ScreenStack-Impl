# Ribs-ScreenStack-Impl
An implementation of ribs screenstack

## How to use:
```java
// Push a new screen
screenStackBase.pushScreen(new ViewProvider() {
      @Override public View buildView(ViewGroup parentView) {
        View root = LayoutInflater.from(context).inflate(R.layout.view_test, parentView, false);
        TextView arbit = root.findViewById(R.id.arbit);
        arbit.setText(String.valueOf(new Random().nextInt(100)));
        root.setOnClickListener(new View.OnClickListener() {
          @Override public void onClick(View view) {
            pushScreen();
          }
        });
        return root;
      }
    });
    
// Pops a screen    
```java
if (screenStackBase.size() > 0) {
      screenStackBase.popScreen();
      return true;
    }
```
