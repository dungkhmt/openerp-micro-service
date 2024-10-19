import styles from "./loading.module.css"

const LoadingScreen = () => {
  return (
    <div className={styles.loaderContainer}>
      <div className={styles.spinner}>
      </div>
    </div>);
}

export default LoadingScreen;